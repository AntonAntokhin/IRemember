package com.antohin.iremember.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * this class need for PhotoView in documentation Issues With ViewGroups
 * https://github.com/chrisbanes/PhotoView
 */

public class HackViewPager extends ViewPager {
    public HackViewPager(@NonNull Context context) {
        super(context);
    }

    public HackViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
