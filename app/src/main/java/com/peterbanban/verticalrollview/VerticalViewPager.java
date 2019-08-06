package com.peterbanban.verticalrollview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;

/**
 * Created by hqx on 2019/8/6 14:30.
 */

public class VerticalViewPager extends ViewPager {
  private boolean banScroll;
  private int dividerSize = 1;

  public VerticalViewPager(@NonNull Context context) {
    this(context, null);
  }

  public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    setPageTransformer(false, new VerticalTransform());
  }

  public void banScroll(boolean ban){
    banScroll = ban;
  }

  public void setDividerSize(int size) {
    dividerSize = size;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (banScroll) {
      switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
          return false;
        default:
          return super.onInterceptTouchEvent(swapXY(ev));
      }
    } else {
      boolean intercept = super.onInterceptTouchEvent(swapXY(ev));
      swapXY(ev);
      return intercept;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return banScroll || super.onTouchEvent(swapXY(ev));
  }

  private MotionEvent swapXY(MotionEvent ev) {
    int width = getWidth();
    int height = getHeight();
    int newX = (int) ((ev.getY() / height) * width);
    int newY = (int) ((ev.getX() / width) * height);
    ev.setLocation(newX, newY);
    return ev;
  }

  class VerticalTransform implements PageTransformer {

    @Override
    public void transformPage(@NonNull View view, float pos) {
      view.setTranslationX(view.getWidth() * -pos);
      view.setTranslationY((view.getHeight() * 1f / dividerSize) * pos);
    }
  }

  public static class VerticalScroll extends Scroller {
    private int DURATION = 500;
    private static Interpolator verticalInterpolator = new FastOutSlowInInterpolator();

    public VerticalScroll(Context context, Interpolator interpolator) {
      super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
      super.startScroll(startX, startY, dx, dy, DURATION);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
      super.startScroll(startX, startY, dx, dy, DURATION);
    }

    public void setDuration(int duration) {
      DURATION = duration;
    }

    public void bindScrollInner(ViewPager viewPager) {
      try {
        Field field = ViewPager.class.getDeclaredField("mScroller");
        field.setAccessible(true);
        field.set(viewPager, this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static void bindScroll(ViewPager viewPager) {
      VerticalScroll verticalScroll = new VerticalScroll(viewPager.getContext(), verticalInterpolator);
      try {
        Field field = ViewPager.class.getDeclaredField("mScroller");
        field.setAccessible(true);
        field.set(viewPager, verticalScroll);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
