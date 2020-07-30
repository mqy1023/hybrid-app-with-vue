//
//  BBMCommon.h
//  BBM
//
//  Created by eric on 2019/10/8.
//  Copyright © 2019 eric. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BBMCommon : NSObject

//NSUserDefault写入
+ (void)BBMSetObject:(id)object forKey:(NSString *)key;

//NSUserDefault读出
+ (id)BBMGetObjectForKey:(NSString *)key;

//NSUserDefault移除
+ (void)BBMRemoveObjectforKey:(NSString *)key;

+ (void)showToast:(NSString *)text;

@end

NS_ASSUME_NONNULL_END
