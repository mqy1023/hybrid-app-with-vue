//
//  AppDelegate.m
//  BBM
//
//  Created by eric on 2019/9/17.
//  Copyright © 2019 eric. All rights reserved.
//

#import "AppDelegate.h"
#import "ViewController.h"
#import <UMCommon/UMCommon.h>
#import "BBMPushManager.h"
#import "Constants.h"
#import "BBMCommon.h"

// 引入 JPush 功能所需头文件
#import "JPUSHService.h"
// iOS10 注册 APNs 所需头文件
#ifdef NSFoundationVersionNumber_iOS_9_x_Max
#import <UserNotifications/UserNotifications.h>
#endif

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    // 极光推送配置
    [[BBMPushManager getInstance] jpushConfig:launchOptions];
    // 友盟配置
    [UMConfigure initWithAppkey:UMENG_APP_KEY channel:APP_CHANNEL];
    
    // Override point for customization after application launch.
    self.window = [[UIWindow alloc]initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    [_window setRootViewController:[ViewController new]];
    [self.window makeKeyAndVisible];
    
    NSLog(@"didFinishLaunchingWithOptions");
    
    return YES;
}


- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    /// Required - 注册 DeviceToken
    [[BBMPushManager getInstance] didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"did Fail To Register For Remote Notifications With Error: %@", error);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    
    // Required, iOS 7 Support
    [[BBMPushManager getInstance] didReceiveRemoteNotification:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
}

// 本地推送事件触发（iOS10之前）
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [application cancelLocalNotification:notification];
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    NSLog(@"applicationWillResignActive");
}

- (void)UIApplicationDidBecomeActiveNotification:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    NSLog(@"UIApplicationDidBecomeActiveNotification");
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    NSLog(@"applicationDidEnterBackground");
    
//    ViewController *vc = (ViewController *)[UIApplication sharedApplication].delegate.window.rootViewController;
//    if (vc && [vc isKindOfClass:ViewController.class]) {
//        [vc jumpH5Url: @"/home"];
//    }
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    //在这个方法里输入如下清除方法
    NSLog(@"applicationWillEnterForeground");
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    NSLog(@"applicationDidBecomeActive");
//    [BBMCommon showToast:@"BecomeActive"];
//    [self removeAllNotification];
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    NSLog(@"applicationWillTerminate");
    [BBMCommon showToast:@"Terminate"];
    [self removeAllNotification];
}

#pragma mark  - 移除所有通知
- (void)removeAllNotification {
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        [center removeAllPendingNotificationRequests];
        [center removeAllDeliveredNotifications];
    } else {
        [[UIApplication sharedApplication] cancelAllLocalNotifications];
    }
}


@end
