package com.peterbanban.verticalrollview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;

/**
 * Created by hqx on 2019/8/8
 */
public class VerticalScroll extends Scroller {
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
