//
//  LaunchView.m
//  BBM
//
//  Created by eric on 2019/9/17.
//  Copyright © 2019 eric. All rights reserved.
//

#import "LaunchView.h"
#import "Constants.h"

@implementation LaunchView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self initializeUI];
    }
    return self;
}

- (void)initializeUI {
    UIScrollView *scrollView = [UIScrollView.alloc initWithFrame:self.bounds];
    [self addSubview:scrollView];
    scrollView.backgroundColor = UIColor.whiteColor;
    scrollView.pagingEnabled = YES;
    
    NSInteger imageCount = 3;
    scrollView.contentSize = CGSizeMake(imageCount*self.frame.size.width, self.frame.size.height);
    
    CGFloat height = self.frame.size.height;
    CGFloat width = self.frame.size.width;
    for (NSInteger i = 0; i < imageCount; i++) {
        UIImageView *imageView = [UIImageView.alloc initWithFrame:CGRectMake(i*width, 0, width, height)];
        NSString *pngName;
        if (IS_iPhoneXSerise) {
            pngName = [NSString stringWithFormat:@"guidex%ld",i+1];
        } else {
            pngName = [NSString stringWithFormat:@"guide%ld",i+1];
        }
        imageView.image = [UIImage imageNamed:pngName];
        imageView.userInteractionEnabled = YES;
        [scrollView addSubview:imageView];
        
        if (i == (imageCount - 1)) {
            CGFloat btnW = 160;
            CGFloat btnH = 46;
            CGFloat btnX = self.frame.size.width*0.5 - btnW*0.5;
            CGFloat btnY = self.frame.size.height - btnH - 60;
            UIButton *closeButton = [UIButton.alloc initWithFrame:CGRectMake(btnX, btnY, btnW, btnH)];
            [imageView addSubview:closeButton];
            
            UIColor *mUIColor = [UIColor colorWithRed:67/255.f green:100/255.f blue:252/255.f alpha:1];
            
            [closeButton setTitle:@"立即体验" forState:UIControlStateNormal];
            [closeButton setTitleColor:mUIColor forState:UIControlStateNormal];
            closeButton.backgroundColor = UIColor.whiteColor;
            
            [closeButton.layer setCornerRadius:22];
            [closeButton.layer setBorderWidth:1.0];
            [closeButton.layer setBorderColor:mUIColor.CGColor];
            [closeButton addTarget:self action:@selector(dismiss) forControlEvents:UIControlEventTouchUpInside];
        }
    }
    
}

- (void)dismiss {
    [self removeFromSuperview];
}

@end
