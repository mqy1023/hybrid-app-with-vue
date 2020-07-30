//
//  Constants.h
//  BBM
//
//  Created by eric on 2019/9/26.
//  Copyright © 2019 eric. All rights reserved.
//

#import <Foundation/Foundation.h>

#ifndef Constants_h
#define Constants_h

//#define BASE_WEB_URL  @"http://daifu.hahaha.com/web#/"
//#define BASE_WEB_URL  @"http://v2.hahaha.vip/web#/"
#define BASE_WEB_URL  @"http://t-v2.hahaha.vip/web#/"
//#define BASE_WEB_URL  @"http://pre.v2.hahaha.vip/web#/"
//#define BASE_WEB_URL  @"http://192.168.1.79:8083/#/"

#define JIGUANG_APP_KEY  @"c5a7ba9fed44dbf15854adb9"

#define UMENG_APP_KEY  @"5e018aa30cafb280aa0002f1"

#define APP_CHANNEL  @"ios1"
#define APP_CHANNEL_NUM  @"1"


#define StatusH        [[UIApplication sharedApplication] statusBarFrame].size.height //状态高度
#define IS_iPhoneXSerise   (StatusH > 20)


#define IsStringEmpty(_string_) (IsObjectEmpty(_string_) || ![_string_ isKindOfClass:NSString.class] || _string_.length == 0)

#define IsObjectEmpty(_obj_) (!_obj_ || (_obj_ == nil) || (_obj_ == NULL) || [_obj_ isKindOfClass:[NSNull class]] || [_obj_ isEqual:[NSNull null]])

#define IsDictionaryEmpty(_dict_) (IsObjectEmpty(_dict_) || ![_dict_ isKindOfClass:NSDictionary.class] || _dict_.count == 0)

#endif /* Constants_h */
