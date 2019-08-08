package com.peterbanban.verticalrollview;

import android.content.Context;
import android.view.View;

/**
 * Created by hqx on 2019/8/8 15:31.
 */
public interface IViewGetter<T,V extends View> {
  V createView(Context context);
  void initView(V view, T bean);
}