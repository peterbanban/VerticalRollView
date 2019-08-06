package com.peterbanban.verticalrollview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.peterbanban.verticalrollview.VerticalViewPager.VerticalScroll;
import java.util.ArrayList;
import java.util.List;

public class VerticalRollView <T, V extends View> extends FrameLayout {
  private VerticalViewPager viewPager;
  private List<T> beans;
  private List<FrameLayout> children;
  private IViewGetter<T, V> iViewGetter;
  private Handler handler;
  private int DELAY_TIME = 5000;
  private int counter;
  private int prePos;
  private int showLength;

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
    VerticalScroll.bindScroll(viewPager);
    children = new ArrayList<>();
    addView(viewPager);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  private void initChild() {
    int height = getMeasuredHeight();
    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height / showLength);
    for (int i = 0; i < beans.size(); i++) {
      V view = iViewGetter.createView(getContext());
      iViewGetter.initView(view, beans.get(i));
      FrameLayout pageWrapper = new FrameLayout(getContext());
      pageWrapper.addView(view, lp);
      view.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
      });
      children.add(pageWrapper);
    }
  }

  private void notifyViewPager() {
    if (beans == null) {
      return;
    }
    // 为了实现循环滚动在尾部添加长度为展示窗口长度的头部数据
    if (beans.size() > showLength) {
      for (int i = 0; i < showLength; i++) {
        beans.add(beans.get(i));
      }
    }
    initChild();
    viewPager.setOffscreenPageLimit(showLength + 2);
    viewPager.banScroll(true);
    viewPager.setDividerSize(showLength);
    viewPager.setAdapter(new PagerAdapter() {
      @Override
      public int getCount() {
        if (beans == null) {
          return 0;
        }
        return beans.size();
      }

      @NonNull
      @Override
      public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = children.get(position);
        container.addView(view);
        return view;
      }

      @Override
      public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
      }

      @Override
      public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(children.get(position));
      }
    });
  }

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      if (counter == beans.size() - (showLength - 1)) {
        prePos = counter;
        counter = 0;
        viewPager.setCurrentItem(0, false);
        handler.post(this);
      } else {
        viewPager.setCurrentItem(counter, true);
        handler.postDelayed(this, prePos == beans.size() - (showLength - 1) ? 0 : DELAY_TIME);
        prePos = counter++;
      }
    }
  };

  public void startRoll() {
    post(new Runnable() {
      @Override
      public void run() {
        notifyViewPager();
      }
    });

    if (beans.size() <= showLength) {
      return;
    }
    handler.removeCallbacks(runnable);
    handler.post(runnable);
  }

  public void stopRoll() {
    handler.removeCallbacks(runnable);
  }

  public void setShowLength(int length) {
    showLength = length;
  }

  public void setDelayTime(int delay) {
    DELAY_TIME = delay;
  }

  public void setIViewGetter(List<T> beans, IViewGetter<T, V> viewGetter) {
    iViewGetter = viewGetter;
    this.beans = beans;
  }

  public interface IViewGetter<T,V extends View> {
    V createView(Context context);
    void initView(V view, T bean);
  }
}