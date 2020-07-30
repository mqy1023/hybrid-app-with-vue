package com.shxbd.bbm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * @Author: 张
 * @Email: 1271396448@qq.com
 * @Date: 2019/7/23 4:51 PM
 * <p>
 * 解决全屏模式下，输入框高度不能自适应，兼容导航栏变化、屏幕旋转
 */
public class GlobalLayoutUtil {

    private Activity activity;
    private boolean isImmersed = false;
    private View mChildOfContent;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int usableHeightPrevious = 0;

    public GlobalLayoutUtil(Activity activity) {
        this.activity = activity;
        init();
    }

    public void init() {
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        //添加布局变化监听

        //界面出现变动都会调用这个监听事件
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent(activity);
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent(Activity activity) {
        //当前可视区域的高度
        int usableHeightNow = computeUsableHeight();
        //当前高度值和之前的进行对比,变化将进行重新绘制
        if (usableHeightNow != usableHeightPrevious) {
            //获取当前屏幕高度
            //Ps: 并不是真正的屏幕高度，是当前app的窗口高度，分屏时的高度为分屏窗口高度
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            // 高度差值：屏幕高度 - 可视内容高度
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            // 差值为负，说明获取屏幕高度时出错，宽高状态值反了，重新计算
            if (heightDifference < 0) {
                usableHeightSansKeyboard = mChildOfContent.getRootView().getWidth();
                heightDifference = usableHeightSansKeyboard - usableHeightNow;
            }
            // keyboard probably just became visible
            // 如果差值大于屏幕高度的 1/4，则认为输入软键盘为弹出状态
            if (heightDifference > usableHeightSansKeyboard / 4) {
                // 设置布局高度为：屏幕高度 - 高度差
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else if (heightDifference >= getNavigationBarHeight(activity)) {
                // keyboard probably just became hidden
                // 虚拟导航栏显示
                frameLayoutParams.height = usableHeightSansKeyboard - getNavigationBarHeight(activity);
            } else {// 其他情况直接设置为可视高度即可
                frameLayoutParams.height = usableHeightNow;
            }
        }
        // 刷新布局，会重新测量、绘制
        mChildOfContent.requestLayout();
        // 保存高度信息
        usableHeightPrevious = usableHeightNow;
    }


    /**
     * 获取可视内容区域的高度
     */
    private int computeUsableHeight() {
        Rect rect = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(rect);
        if (isImmersed)
            return rect.bottom;
        else
            return rect.bottom - rect.top;
    }

    /**
     * 获取导航栏的真实高度
     *
     * @param context:
     * @return: 导航栏高度
     */
    private int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources resources = context.getResources();
        int resourceId =
                resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = resources.getDimensionPixelSize(resourceId);
        return result;
    }

}