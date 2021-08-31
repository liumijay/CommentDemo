package com.example.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 *   防止评论弹窗快速滑动到底部或者顶部后，第一次点击失效的问题
 */
public class FixCommentDialogBehavior extends BottomSheetBehavior<FrameLayout> {
    public FixCommentDialogBehavior() {
        super();
    }

    public FixCommentDialogBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FrameLayout child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        if (type==ViewCompat.TYPE_TOUCH){
            ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
        }
    }

}
