//
//  BBMPushModel.m
//  BBM
//
//  Created by eric on 2019/10/10.
//  Copyright © 2019 eric. All rights reserved.
//

#import "BBMPushModel.h"

@implementation BBMPushModel

- (void)configWithDictionary:(NSDictionary *)dictionary {
    
    self.message_id = [dictionary objectForKey:@"message_id"];
    self.title = [dictionary objectForKey:@"title"];
    self.content = [dictionary objectForKey:@"content"];
    self.jump_url = [dictionary objectForKey:@"jump_url"];
}

@end
