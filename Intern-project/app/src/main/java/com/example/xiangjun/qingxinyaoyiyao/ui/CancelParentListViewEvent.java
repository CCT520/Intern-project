package com.example.xiangjun.qingxinyaoyiyao.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import java.util.List;

/**
 * Created by xiangjun on 15/12/29.
 */

//这个类用来取消父listview的动作监听，从而让嵌套的listview可以上下滑动

public class CancelParentListViewEvent extends ListView {
    public CancelParentListViewEvent(Context context) {
        super(context);
    }

    public CancelParentListViewEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelParentListViewEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //将 onInterceptTouchEvent的返回值设置为false，取消其对触摸事件的处理，将事件分发给子view

    @Override

    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;

    }
}
