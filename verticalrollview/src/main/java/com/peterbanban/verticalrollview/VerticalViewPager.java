package com.peterbanban.verticalrollview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {
  private int dividerSize = 1;

  public VerticalViewPager(@NonNull Context context) {
    this(context, null);
  }

  public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setPageTransformer(false, new VerticalTransform());
  }

  public void setDividerSize(int size) {
    dividerSize = size;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
      switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
          return false;
        default:
          return super.onInterceptTouchEvent(ev);
      }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return true;
  }

  class VerticalTransform implements PageTransformer {

    @Override
    public void transformPage(@NonNull View view, float pos) {
      view.setTranslationX(view.getWidth() * -pos);
      view.setTranslationY((view.getHeight() * 1f / dividerSize) * pos);
    }
  }
}
