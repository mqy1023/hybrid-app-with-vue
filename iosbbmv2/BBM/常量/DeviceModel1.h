//
//  DeviceModel.h
//  BBM
//
//  Created by eric on 2019/9/26.
//  Copyright © 2019 eric. All rights reserved.
//

#import <sys/utsname.h>

#ifndef DeviceModel_h
#define DeviceModel_h


- (NSString *)iphoneType {
    struct utsname systemInfo;
    uname(&systemInfo);
    
    NSString *platform = [NSString stringWithCString:systemInfo.machine encoding:NSASCIIStringEncoding];
    
    if ([platform isEqualToString:@"iPhone1,1"]) return @"iPhone 2G";
    
    if ([platform isEqualToString:@"iPhone1,2"]) return @"iPhone 3G";
    
    if ([platform isEqualToString:@"iPhone2,1"]) return @"iPhone 3GS";
    
    if ([platform isEqualToString:@"iPhone3,1"]) return @"iPhone 4";
    
    if ([platform isEqualToString:@"iPhone3,2"]) return @"iPhone 4";
    
    if ([platform isEqualToString:@"iPhone3,3"]) return @"iPhone 4";
    
    if ([platform isEqualToString:@"iPhone4,1"]) return @"iPhone 4S";
    
    if ([platform isEqualToString:@"iPhone5,1"]) return @"iPhone 5";
    
    if ([platform isEqualToString:@"iPhone5,2"]) return @"iPhone 5";
    
    if ([platform isEqualToString:@"iPhone5,3"]) return @"iPhone 5c";
    
    if ([platform isEqualToString:@"iPhone5,4"]) return @"iPhone 5c";
    
    if ([platform isEqualToString:@"iPhone6,1"]) return @"iPhone 5s";
    
    if ([platform isEqualToString:@"iPhone6,2"]) return @"iPhone 5s";
    
    if ([platform isEqualToString:@"iPhone7,1"]) return @"iPhone 6 Plus";
    
    if ([platform isEqualToString:@"iPhone7,2"]) return @"iPhone 6";
    
    if ([platform isEqualToString:@"iPhone8,1"]) return @"iPhone 6s";
    
    if ([platform isEqualToString:@"iPhone8,2"]) return @"iPhone 6s Plus";
    
    if ([platform isEqualToString:@"iPhone8,4"]) return @"iPhone SE";
    
    if ([platform isEqualToString:@"iPhone9,1"]) return @"iPhone 7";//国行、日版、港行
    
    if ([platform isEqualToString:@"iPhone9,2"]) return @"iPhone 7 Plus";//港行、国行
    if ([platform isEqualToString:@"iPhone9,3"])    return @"iPhone 7";//美版、台版
    if ([platform isEqualToString:@"iPhone9,4"])    return @"iPhone 7 Plus";//美版、台版
    
    if ([platform isEqualToString:@"iPhone10,1"])   return @"iPhone 8";//国行(A1863)、日行(A1906)
    
    if ([platform isEqualToString:@"iPhone10,4"])   return @"iPhone 8";//美版(Global/A1905)
    
    if ([platform isEqualToString:@"iPhone10,2"])   return @"iPhone 8 Plus";//国行(A1864)、日行(A1898)
    
    if ([platform isEqualToString:@"iPhone10,5"])   return @"iPhone 8 Plus";//美版(Global/A1897)
    
    if ([platform isEqualToString:@"iPhone10,3"])   return @"iPhone X";//国行(A1865)、日行(A1902)
    
    if ([platform isEqualToString:@"iPhone10,6"])   return @"iPhone X";//美版(Global/A1901)
    
    if ([platform isEqualToString:@"iPhone11,8"])   return @"iPhone XR";
    if ([platform isEqualToString:@"iPhone11,2"])   return @"iPhone XS";
    if ([platform isEqualToString:@"iPhone11,6"])   return @"iPhone XS Max";
    if ([platform isEqualToString:@"iPhone11,4"])   return @"iPhone XS Max";
    
    return platform;
}


#endif /* DeviceModel_h */
