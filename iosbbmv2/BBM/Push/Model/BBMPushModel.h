//
//  BBMPushModel.h
//  BBM
//
//  Created by eric on 2019/10/10.
//  Copyright © 2019 eric. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BBMPushModel : NSObject

@property (nonatomic, assign) NSInteger message_id; // 消息id
@property (nonatomic, copy) NSString *title; // 标题
@property (nonatomic, copy) NSString *content; // 内容
@property (nonatomic, copy) NSString *jump_url; // 跳转url

- (void)configWithDictionary:(NSDictionary *)dictionary;

@end

NS_ASSUME_NONNULL_END
