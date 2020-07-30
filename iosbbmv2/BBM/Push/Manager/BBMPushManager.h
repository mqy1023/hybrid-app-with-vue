//
//  BBMPushManager.h
//  BBM
//
//  Created by eric on 2019/10/8.
//  Copyright © 2019 eric. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol JPUSHRegisterDelegate;

@interface BBMPushManager : NSObject<JPUSHRegisterDelegate>

+ (instancetype)getInstance;

- (void)jpushConfig:(NSDictionary *)launchOptions;

- (void)openPushNoti;

- (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;

- (void)didReceiveRemoteNotification:(NSDictionary *)userInfo;

/** 判断是否打开推送 */
+ (BOOL)isUserNotificationEnable;

/** 获取极光设备id **/
+ (NSString *)getRegistrationID;

@end

NS_ASSUME_NONNULL_END
