package com.peterbanban.verticalrollview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * provide a view to display banner indirect scroll vertically
 * support roll up and roll down
 * @param <T> data type
 * @param <V> view type
 * Created by hqx on 2019/8/8
 */
public class VerticalRollView <T, V extends View> extends FrameLayout {
  private VerticalViewPager viewPager;
  private List<T> beans;
  private IViewGetter<T, V> iViewGetter;
  private Handler handler;
  private Runnable rollTask;
  private VerticalScroll verticalScroll;
  private boolean rollDown;
  private int DELAY_TIME = 5000;
  private int DURATION = 600;
  private int counter;
  private int prePos;
  private int showLength = 1;
  private FrameLayout.LayoutParams lp;

  public VerticalRollView(@NonNull Context context) {
    this(context, null);
  }

  public VerticalRollView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VerticalRollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  private void initView() {
    viewPager = new VerticalViewPager(getContext());
    handler = new Handler();
    verticalScroll = new VerticalScroll(getContext(), new LinearOutSlowInInterpolator());
    verticalScroll.setDuration(500);
    verticalScroll.bindScrollInner(viewPager);
    addView(viewPager);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  private void initChild() {
    if (!rollDown) {
      fillDataPositive();
    } else {
      fillDataNegative();
    }
  }

  private void fillDataPositive() {
    for (int i = 0; i < showLength; i ++) {
      beans.add(beans.get(i));
    }
    counter = 0;
  }

  private void fillDataNegative() {
    ArrayList<T> newList = new ArrayList<>();
    for (int i = 0; i < showLength; i ++) {
      newList.add(beans.get(showLength - i - 1));
    }
    for (int i = beans.size() - 1; i >= 0; i--) {
      newList.add(beans.get(i));
    }
    beans.clear();
    beans = newList;
    counter = -1;
  }

  private void notifyViewPager() {
    if (beans == null || beans.size() <= showLength) {
      throw new RuntimeException("beans is null or beans' size less than showLength");
    }

    initChild();
    lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, getMeasuredHeight() / showLength);
    viewPager.setOffscreenPageLimit(2);
    viewPager.setDividerSize(showLength);
    viewPager.setCurrentItem(counter);
    viewPager.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        return beans.size();
      }

      @NonNull
      @Override
      public Object instantiateItem(@NonNull ViewGroup container, int position) {
        V view = iViewGetter.createView(getContext());
        iViewGetter.initView(view, beans.get(position));
        FrameLayout pageWrapper = new FrameLayout(getContext());
        pageWrapper.addView(view, lp);
        container.addView(pageWrapper);
        return pageWrapper;
      }

      @Override
      public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
      }

      @Override
      public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
      }
    });
  }

  public void startRoll() {
    rollTask = new Runnable() {
      @Override
      public void run() {
        if (!rollDown) {
          if (counter == beans.size() - (showLength - 1)) {
            prePos = counter;
            counter = 0;
            verticalScroll.setDuration(0);
            viewPager.setCurrentItem(0, true);
            handler.postDelayed(this, 2000);
          } else {
            if (prePos != beans.size() - (showLength - 1)) {
              verticalScroll.setDuration(DURATION);
              viewPager.setCurrentItem(counter, true);
            }
            handler.postDelayed(this, prePos == beans.size() - (showLength - 1) ? 0 : DELAY_TIME);
            prePos = counter++;
          }
        } else {
          if (counter == -1) {
            prePos = counter;
            counter = beans.size() - showLength;
            verticalScroll.setDuration(0);
            viewPager.setCurrentItem(beans.size() - showLength, true);
            handler.post(this);
          } else {
            if (prePos != -1) {
              verticalScroll.setDuration(DURATION);
              viewPager.setCurrentItem(counter, true);
            }
            handler.postDelayed(this, prePos == -1 ? 0 : DELAY_TIME);
            prePos = counter--;
          }
        }
      }
    };

    post(new Runnable() {
      @Override
      public void run() {
        notifyViewPager();
      }
    });

    if (beans.size() <= showLength) {
      return;
    }
    handler.removeCallbacks(rollTask);
    handler.post(rollTask);
  }

  public void stopRoll() {
    handler.removeCallbacks(rollTask);
  }

  public void setRollDown(boolean down) {
    rollDown = down;
  }

  public void setShowLength(int length) {
    showLength = length;
  }

  public void setDelayTime(int delay) {
    DELAY_TIME = delay;
  }

  public void setDuration(int duration) {
    DURATION = duration;
  }

  public void setIViewGetter(List<T> beans, IViewGetter<T, V> viewGetter) {
    iViewGetter = viewGetter;
    this.beans = new ArrayList<>(beans);
  }
}