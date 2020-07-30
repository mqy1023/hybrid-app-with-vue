//
//  ViewController.m
//  BBM
//
//  Created by eric on 2019/9/17.
//  Copyright © 2019 eric. All rights reserved.
//

#import "ViewController.h"
#import <WebKit/WebKit.h>
// 引入 JPush 功能所需头文件
#import "JPUSHService.h"
#import "LaunchView.h"
#import "BBMCommon.h"
#import "Constants.h"
#import "IphoneModel.h"

// 引入 JPush 功能所需头文件
#import "JPUSHService.h"
// iOS10 注册 APNs 所需头文件
#ifdef NSFoundationVersionNumber_iOS_9_x_Max
#import <UserNotifications/UserNotifications.h>
#endif

@interface ViewController ()<WKNavigationDelegate,WKUIDelegate, WKScriptMessageHandler>
@property (nonatomic, strong) WKWebView *webView;
@property (nonatomic, strong) WKWebViewConfiguration *wkWebViewConfiguration;
@property (nonatomic, strong) WKUserContentController *wkUserContentController;
@property (nonatomic, strong) UIImageView *iv;
@property (nonatomic, strong) NSUserDefaults *userDefaults;
@property (nonatomic, strong) LaunchView *launchView;

@end

@implementation ViewController

- (UIImageView *)iv {
    if (!_iv) {
        _iv = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"welcome.png"]];
        _iv.frame = self.view.bounds;
    }
    return _iv;
}

- (LaunchView *)launchView {
    if (!_launchView) {
        _launchView = [LaunchView.alloc initWithFrame:self.view.bounds];
    }
    return _launchView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = UIColor.whiteColor; // 底色白色
    
    [self initWebView];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
}

- (void)initWebView {
    
    //配置wkWebViewConfiguration
    [self wkConfiguration];
    
    CGFloat screenW = [UIScreen mainScreen].bounds.size.width;
    CGFloat screenH = [UIScreen mainScreen].bounds.size.height;
    CGRect webFrame;
    CGFloat topMargin = 20;
    CGFloat bottomMargin = 0;
    if (IS_iPhoneXSerise) {
        topMargin = 40;
    }
    CGFloat height = screenH - topMargin - bottomMargin;
    webFrame = CGRectMake(0, topMargin, screenW, height);
    
    //初始化webView
    self.webView = [[WKWebView alloc] initWithFrame:webFrame configuration:self.wkWebViewConfiguration];
    self.webView.backgroundColor = [UIColor whiteColor];
    
    //关闭左划回退功能
    self.webView.allowsBackForwardNavigationGestures = NO;
    self.webView.navigationDelegate = self;
    self.webView.UIDelegate = self;
    
    NSString *iphoneModel = [IphoneModel getIphoneType];
    NSLog(@"手机型号1: %@", iphoneModel);
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *appVersion = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    NSLog(@"app版本1: %@", appVersion);
    
    NSTimeInterval recordTime = [[NSDate date] timeIntervalSince1970];
    
    NSString *timeStamp = [NSString stringWithFormat:@"%ld",(long)recordTime];
    
    NSString *registrationID = [self getRegistrationID];
    
    NSString *urlParams = [[NSString alloc] initWithFormat:@"version=%@&src=%@&jgid=%@&model=%@&t=%@", appVersion, APP_CHANNEL_NUM, registrationID, iphoneModel, timeStamp];
    urlParams = [urlParams stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
 
    // 登录过了跳转首页不是玩法页面
    NSString *openType = @"0";
    openType = [[NSUserDefaults standardUserDefaults] objectForKey:@"openTypeKey"];
    
    NSString *openUrl;
    if ([openType isEqualToString: @"1"]) { // 一级代理打开home
        openUrl = [[NSString alloc] initWithFormat:@"%@%@?%@", BASE_WEB_URL, @"home", urlParams];
    } else if ([openType isEqualToString: @"2"]) { // 一级代理打开dispatch
        openUrl = [[NSString alloc] initWithFormat:@"%@%@?%@", BASE_WEB_URL, @"dispatch", urlParams];
    } else {
        openUrl = [[NSString alloc] initWithFormat:@"%@%@?%@", BASE_WEB_URL, @"login", urlParams];
    }


    NSLog(@"appopenUrl: %@", openUrl);
    
    //    设置 URL 访问，如要修改访问地址，需要修改 常量/Constants.h/BASE_WEB_URL
    NSURL *url = [NSURL URLWithString:openUrl];
    NSURLRequest *request = [NSURLRequest requestWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:800];
    [self.webView loadRequest:request];
    
    //解决 iOS 11 屏幕顶部显示不全
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 11.0) {
        
        self.webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
    
    [self.view addSubview:self.webView];
    [self.view addSubview:self.iv];
    
    NSString *firstKey = @"firstKey";
    NSNumber *firstNum = [[NSUserDefaults standardUserDefaults] objectForKey:firstKey];
    if (!firstNum) {
        [[NSUserDefaults standardUserDefaults] setObject:@(1) forKey:firstKey];
        [self.view addSubview:self.launchView];
    }
    
    //OC注册供JS调用的方法
    [self addScriptFunction];
}

- (void)wkConfiguration {
    self.wkWebViewConfiguration = [[WKWebViewConfiguration alloc]init];
    
    WKPreferences *preferences = [[WKPreferences alloc] init];
    preferences.javaScriptCanOpenWindowsAutomatically = YES;
    self.wkWebViewConfiguration.preferences = preferences;
    
}

#pragma mark - WKWebView
// 页面开始加载时调用
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    NSLog(@"start : %@",webView.URL.absoluteString);
}
// 当内容开始返回时调用
- (void)webView:(WKWebView *)webView didCommitNavigation:(WKNavigation *)navigation {
    NSLog(@"");
}


// 页面加载完成之后调用
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    //    关闭欢迎图片
    self.iv.hidden = YES;
    
    NSString *registrationID = [self getRegistrationID];
    
    NSString *aa = [[NSString alloc] initWithFormat:@"registrationID1=%@", registrationID];
    NSLog(aa);
//    [self.webView evaluateJavaScript:[NSString stringWithFormat:@"callByNativeJump('%@')", @"/user"] completionHandler:nil];
    
//    [self isAutoLogin];
}
// 页面加载失败时调用
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(WKNavigation *)navigation {
    NSLog(@"fail");
}

// 接收到服务器跳转请求之后调用
- (void)webView:(WKWebView *)webView didReceiveServerRedirectForProvisionalNavigation:(WKNavigation *)navigation{
    NSLog(@"");
}

#pragma mark -  OC注册供JS调用的方法
- (void)addScriptFunction {
    self.wkUserContentController = [self.webView configuration].userContentController;
    
    [self.wkUserContentController addScriptMessageHandler:self name:@"setOpenType"]; // 0打开登录页，1打开一级代理home，2打开二级代理dispatch
    [self.wkUserContentController addScriptMessageHandler:self name:@"setJPushAlias"]; // 设置别名
    [self.wkUserContentController addScriptMessageHandler:self name:@"setJPushTag"]; // 设置tag标签
    
    [self.wkUserContentController addScriptMessageHandler:self name:@"jumpBrowser"]; // 跳转浏览器
    
    [self.wkUserContentController addScriptMessageHandler:self name:@"clearUserId"]; // h5退出登录
    
    [self.wkUserContentController addScriptMessageHandler:self name:@"getJPushId"]; // 获取极光id
    
    [self.wkUserContentController addScriptMessageHandler:self name:@"isBbmAgentApp"]; // 是否是我们的app
    
//    [self.wkUserContentController addScriptMessageHandler:self name:@"logout"];
}


#pragma mark --- WKScriptMessageHandler ---
//OC在JS调用方法做的处理
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message
{
    // 根据name做想做的操作
    // 前端主动JS发送消息，前端指令动作
    if ([@"setOpenType" isEqualToString:message.name]) { // 0打开登录页，1打开一级代理home，2打开二级代理dispatch
        NSString *openType = [NSString stringWithFormat:@"%@", message.body];
//        NSNumber *loginNum = [[NSUserDefaults standardUserDefaults] objectForKey:openTypeKey];
//        if (!loginNum) {
        [[NSUserDefaults standardUserDefaults] setObject:openType forKey:@"openTypeKey"];
//        }
    } else if ([@"setJPushAlias" isEqualToString:message.name]) { // 设置别名
        NSString *aliasId = [NSString stringWithFormat:@"%@", message.body];
        NSLog(@"设置别名1_%@", aliasId);
        [JPUSHService setAlias:aliasId completion:^(NSInteger iResCode, NSString *iAlias, NSInteger seq) {
                        NSLog(@"%d",iResCode);
                        if (iResCode == 0) {
                                NSLog(@"添加别名成功");
                            }
                    } seq:1];
//        [self login:message.body];
    } else if ([@"setJPushTag" isEqualToString:message.name]) { // 设置tag标签
        NSString *tag = [NSString stringWithFormat:@"%@", message.body];
        NSLog(@"设置tag1_%@", tag);
        NSSet *set = [NSSet setWithObject:tag];
        [JPUSHService setTags:set callbackSelector:nil object:self];
    } else if ([@"jumpBrowser" isEqualToString:message.name]) {
        [self jumpBrowser:message.body];
    } else if ([@"clearUserId" isEqualToString:message.name]) { //
        [[UIApplication sharedApplication] setApplicationIconBadgeNumber:1]; // 重置推送交标为0
        [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0]; // 重置推送交标为0
        [self removeAllNotification];
    } else if ([@"getJPushId" isEqualToString:message.name]) { // 设置极光id
        NSString *registrationID = [self getRegistrationID];
        [self setJgId: registrationID];
    } else if ([@"isBbmAgentApp" isEqualToString:message.name]) { // isBbmAgentApp
        [self callJsMethod: message.body];
    }
}

#pragma mark - isBbmAgentApp
- (void)callJsMethod:(id)body {
    [self.webView evaluateJavaScript:@"callByNativeIsBbmAgentApp(true)" completionHandler:nil];
}

#pragma mark - 跳转H5页面
- (void)jumpH5Url:(NSString *)jumpUrl {
    NSLog(@"jumpH5Url");
    if (!IsStringEmpty(jumpUrl)) {
        [self.webView evaluateJavaScript:[NSString stringWithFormat:@"callByNativeJump('%@')", jumpUrl] completionHandler:nil];
    }
}
#pragma mark - 设置极光id
- (void)setJgId:(NSString *)jgId {
    NSLog(@"jgId");
    if (!IsStringEmpty(jgId)) {
        [self.webView evaluateJavaScript:[NSString stringWithFormat:@"callByNativeJgId('%@')", jgId] completionHandler:nil];
    }
}
#pragma mark - 跳转Safari, 可用来更新app
- (void)jumpBrowser:(NSString *)jumpUrl {
    if (!IsStringEmpty(jumpUrl)) {
        NSURL *url = [ [ NSURL alloc ] initWithString: jumpUrl ];
        [[UIApplication sharedApplication] openURL:url];
    }
}

#pragma mark  - 移除所有通知
- (void)removeAllNotification {
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        [center removeAllPendingNotificationRequests];
    } else {
        [[UIApplication sharedApplication] cancelAllLocalNotifications];
    }
}

#pragma mark  - getRegistrationID
- (NSString *)getRegistrationID {
    NSString *idStr = [BBMCommon BBMGetObjectForKey:@"registrationID"];
    if (IsStringEmpty(idStr)) {
        idStr = @"";
    }
    return idStr;
}

@end
