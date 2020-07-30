//
//  BBMPushManager.m
//  BBM
//
//  Created by eric on 2019/10/8.
//  Copyright © 2019 eric. All rights reserved.
//

#import "BBMPushManager.h"
#import "Constants.h"
#import "BBMCommon.h"
#import "BBMPushModel.h"
#import "ViewController.h"

#import <AdSupport/AdSupport.h> // IDFA

// 引入 JPush 功能所需头文件
#import "JPUSHService.h"
// iOS10 注册 APNs 所需头文件
#ifdef NSFoundationVersionNumber_iOS_9_x_Max
#import <UserNotifications/UserNotifications.h>
#endif

//static NSString *appKey = @"dce65353d4d6e13026a8bd3a"; //推送的appkey
//static NSString *channelVal = @"ios1"; //推送的appkey
#ifdef DEBUG
static BOOL isProduction = FALSE; //0（默认值）表示采用的是开发证书，1 表示采用生产证书发布应用。
#else
static BOOL isProduction = TRUE;
#endif


@interface BBMPushManager ()<UNUserNotificationCenterDelegate, JPUSHRegisterDelegate>

@property (nonatomic, strong) NSDictionary *launchOptions;

@end

@implementation BBMPushManager

+ (instancetype)getInstance {
    static BBMPushManager *_instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[self alloc] init];
    });
    return _instance;
}

- (instancetype)init {
    if (self = [super init]) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(networkDidReceiveMessage:) name:kJPFNetworkDidReceiveMessageNotification object:nil];
    }
    return self;
}

#pragma mark - 极光推送设置
- (void)jpushConfig:(NSDictionary *)launchOptions {
    
    [self removeAllNotification];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0]; // 重置推送交标为0
    
    // 先保存launchOptions
    // 等是否打开推送设置时再继续
    self.launchOptions = launchOptions;
    
    NSLog(@"1111");
    
    // 如果之前已经打开过直接设置极光（没有打开首页会提示打开）
    [self openPushNoti];
}

- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    /// Required - 注册 DeviceToken
    [JPUSHService registerDeviceToken:deviceToken];
    
    [JPUSHService registrationIDCompletionHandler:^(int resCode, NSString *registrationID) {
        NSLog(@"resCode : %d,registrationID: %@",resCode,registrationID);
        [BBMCommon BBMSetObject:registrationID forKey:@"registrationID"];
        ViewController *vc = (ViewController *)[UIApplication sharedApplication].delegate.window.rootViewController;
        if (vc && [vc isKindOfClass:ViewController.class]) {
            [vc setJgId: registrationID];
        }
    }];
}

- (void)openPushNoti {
    NSLog(@"极光推送JPush - 初始化");
    // 添加初始化 APNs 代码
    JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
    entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound;
    [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];
    
    // 添加初始化 JPush 代码
    NSString *idfa = [[[ASIdentifierManager sharedManager] advertisingIdentifier] UUIDString];
    if (IsStringEmpty(idfa)) {
        idfa = @"0";
    }
    [JPUSHService setupWithOption:self.launchOptions appKey:JIGUANG_APP_KEY channel:APP_CHANNEL apsForProduction:isProduction advertisingIdentifier:idfa];
    
    // 注册本地通知
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        center.delegate = self;
        [center requestAuthorizationWithOptions:(UNAuthorizationOptionSound | UNAuthorizationOptionAlert) completionHandler:^(BOOL granted, NSError * _Nullable error) {
            if (!error) {
                NSLog(@"本地推送 - 初始化 - 成功");
            } else {
                NSLog(@"本地推送 - 初始化 - 失败%@",error);
            }
        }];
        [center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
            
        }];
    } else {
        UIUserNotificationSettings *setting = [UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:setting];
    }
}

#pragma mark - 接受自定义消息
- (void)networkDidReceiveMessage:(NSNotification *)notification {
    NSDictionary * userInfo = [notification userInfo];
    [self didReceiveRemoteNotification:userInfo];
}

- (void)didReceiveRemoteNotification:(NSDictionary *)userInfo {
    [JPUSHService handleRemoteNotification:userInfo];
    // 本地逻辑处理
    [self handlePushActionWithDict:userInfo];
}

#pragma mark - 事件交互处理
// 处理推送通知
- (void)handlePushActionWithDict:(NSDictionary *)dict {
    if (IsDictionaryEmpty(dict)) {
        return;
    }
    NSLog(@"极光推送JPush - 内容:\n%@",dict);

    NSDictionary *notiDataStr = [dict objectForKey:@"extras"];
    
    BBMPushModel *pushItem = [[BBMPushModel alloc] init];
    [pushItem configWithDictionary:notiDataStr];
    
    NSMutableDictionary *targetData = [notiDataStr mutableCopy];

    // 发送本地通知
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        UNMutableNotificationContent *content = UNMutableNotificationContent.alloc.init;
        content.title = pushItem.title;
        content.body = pushItem.content;
        content.userInfo = targetData;
        
        NSString *identifier = [NSString stringWithFormat:@"competition_%d", pushItem.message_id];
        
        content.sound = [UNNotificationSound defaultSound];
        NSTimeInterval time = [[NSDate date] timeIntervalSinceNow] + 1;
        UNTimeIntervalNotificationTrigger *trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:time repeats:NO];
        UNNotificationRequest *request = [UNNotificationRequest requestWithIdentifier:identifier content:content trigger:trigger];
        [center addNotificationRequest:request withCompletionHandler:^(NSError * _Nullable error) {
            NSLog(@"本地推送 - 发送 - 成功%@",error);
        }];
    } else {
        // Fallback on earlier versions
        UILocalNotification *notif = [[UILocalNotification alloc] init];
        // 发出推送的日期
        notif.fireDate = [NSDate dateWithTimeIntervalSinceNow:0];
        // 推送的内容
        notif.alertBody = pushItem.content;
        // 可以添加特定信息
        notif.userInfo = targetData;
        // 角标
        //                notif.applicationIconBadgeNumber = 1;
        // 提示音
        notif.soundName = UILocalNotificationDefaultSoundName;
        // 每周循环提醒
        //                notif.repeatInterval = NSCalendarUnitWeekOfYear;
        
        [[UIApplication sharedApplication] scheduleLocalNotification:notif];
    }
}

#pragma mark - 自定义方法
+ (BOOL)isUserNotificationEnable { // 判断用户是否允许接收通知
    BOOL isEnable = NO;
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 8.0f) { // iOS版本 >=8.0 处理逻辑
        UIUserNotificationSettings *setting = [[UIApplication sharedApplication] currentUserNotificationSettings];
        isEnable = (UIUserNotificationTypeNone == setting.types) ? NO : YES;
    } else { // iOS版本 <8.0 处理逻辑
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated"
        UIRemoteNotificationType type = [[UIApplication sharedApplication] enabledRemoteNotificationTypes];
        isEnable = (UIRemoteNotificationTypeNone == type) ? NO : YES;
#pragma clang diagnostic pop
    }
    return isEnable;
}
#pragma mark - 获取极光设备id
+ (NSString *)getRegistrationID {
    NSString *idStr = [BBMCommon BBMGetObjectForKey:@"registrationID"];
    if (IsStringEmpty(idStr)) {
        idStr = @"";
    }
    return idStr;
}


#pragma mark- JPUSHRegisterDelegate
// iOS 12 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification{
    if (notification && [notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //从通知界面直接进入应用
    }else{
        //从通知设置界面进入应用
    }
}
// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler {
    // Required
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
    }
    completionHandler(UNNotificationPresentationOptionAlert); // 需要执行这个方法，选择是否提醒用户，有 Badge、Sound、Alert 三种类型可以选择设置
}

// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler {
    // Required
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
//        [JPUSHService handleRemoteNotification:userInfo];
        [self didReceiveRemoteNotification:userInfo];
    }
    completionHandler();  // 系统要求执行这个方法
}
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    
    // Required, iOS 7 Support
    [JPUSHService handleRemoteNotification:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    
    // Required, For systems with less than or equal to iOS 6
    [JPUSHService handleRemoteNotification:userInfo];
}

#pragma mark - 本地推送相关
// 将要弹出
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler  API_AVAILABLE(ios(10.0)){
//    UIApplicationState state = [UIApplication sharedApplication].applicationState;
//    if (state == UIApplicationStateActive) {
//        // 当前应用在前台，不显示
//        completionHandler(UNNotificationPresentationOptionNone);
//        return;
//    }
    completionHandler(UNNotificationPresentationOptionSound + UNNotificationPresentationOptionAlert);
}
// 点击通知
- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)(void))completionHandler  API_AVAILABLE(ios(10.0)){
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNTimeIntervalNotificationTrigger class]]) {
        [self handleLocalPushActionWithDict:userInfo];
        NSString *identifier = [userInfo objectForKey:@"identifier"];
        [self removeOneNotificationWithID:identifier];
    }
    completionHandler();
}

// 处理本地通知
- (void)handleLocalPushActionWithDict:(NSDictionary *)dict {
    if (IsDictionaryEmpty(dict)) {
        return;
    }
    NSLog(@"点击本地通知");
    NSLog(@"jump_url%@", dict[@"jump_url"]);
    ViewController *vc = (ViewController *)[UIApplication sharedApplication].delegate.window.rootViewController;
    if (vc && [vc isKindOfClass:ViewController.class]) {
        [vc jumpH5Url: dict[@"jump_url"]];
    }
}

// 移除某一个指定的通知
- (void)removeOneNotificationWithID:(NSString *)noticeId {
    if (!IsStringEmpty(noticeId)) {
        return;
    }
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        [center getPendingNotificationRequestsWithCompletionHandler:^(NSArray<UNNotificationRequest *> * _Nonnull requests) {
            for (UNNotificationRequest *req in requests){
                if ([req.identifier isEqualToString:noticeId]) {
                    [center removePendingNotificationRequestsWithIdentifiers:@[noticeId]];
                }
            }
        }];
    } else {
        NSArray *array = [[UIApplication sharedApplication] scheduledLocalNotifications];
        for (UILocalNotification *localNotification in array) {
            NSDictionary *userInfo = localNotification.userInfo;
            NSString *obj = [userInfo objectForKey:@"noticeId"];
            if ([obj isEqualToString:noticeId]) {
                [[UIApplication sharedApplication] cancelLocalNotification:localNotification];
            }
        }
    }
}

// 移除所有通知
- (void)removeAllNotification {
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        [center removeAllPendingNotificationRequests];
    } else {
        [[UIApplication sharedApplication] cancelAllLocalNotifications];
    }
}

@end
