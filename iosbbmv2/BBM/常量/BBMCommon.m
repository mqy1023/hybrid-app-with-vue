//
//  BBMCommon.m
//  BBM
//
//  Created by eric on 2019/10/8.
//  Copyright © 2019 eric. All rights reserved.
//

#import "BBMCommon.h"
#import <UIKit/UIKit.h>
#import "Constants.h"

#define BBMUserDefaults [NSUserDefaults standardUserDefaults]

@implementation BBMCommon

#pragma mark UserDefault id类型写入
+ (void)BBMSetObject:(id)object forKey:(NSString *)key
{
    if (IsObjectEmpty(object)) {
        [BBMUserDefaults setObject:nil forKey:key];
    } else {
        [BBMUserDefaults setObject:object forKey:key];
    }
    [BBMUserDefaults synchronize];
}

#pragma mark UserDefault id类型读出
+ (id)BBMGetObjectForKey:(NSString *)key
{
    return [BBMUserDefaults objectForKey:key];
}

#pragma mark UserDefault 移除
+ (void)BBMRemoveObjectforKey:(NSString *)key
{
    [BBMUserDefaults removeObjectForKey:key];
    [BBMUserDefaults synchronize];
}

+ (void)showToast:(NSString *)text {
    UILabel *label = UILabel.alloc.init;
    label.text = text;
    label.backgroundColor = [UIColor.blackColor colorWithAlphaComponent:0.8];
    label.textColor = UIColor.whiteColor;
    label.layer.cornerRadius = 5;
    label.layer.masksToBounds = YES;
    [label sizeToFit];
    label.alpha = 0;
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [window addSubview:label];
    label.center = window.center;
    
    [UIView animateWithDuration:0.2 animations:^{
        label.alpha = 1;
    } completion:^(BOOL finished) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [UIView animateWithDuration:0.2 animations:^{
                label.alpha = 0;
            } completion:^(BOOL finished) {
                [label removeFromSuperview];
            }];
        });
    }];
}

@end
