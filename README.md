# VerticalRollView
* 竖直滚动的banner组件, 支持向上或向下滚动，可自定义滚动延时和窗口展示View数量
* 实现 
	* 使用ViewPager实现，给ViewPager 设置PageTransformer，重写方法transformPage()使其水平方向位移抵消原有滑动距离，竖直方向偏移距离视展示个数决定，dividerSize是一个轮播窗口可展示的view个数（ps：transformPage方法pos参数是指view的相对位置，当前view的pos是0，左view是-1，右view是1, 往左滑当前view由0到-1，往右滑由0到1）
	```
	 public void transformPage(@NonNull View view, float pos) {
      view.setTranslationX(view.getWidth() * -pos);
      view.setTranslationY((view.getHeight() * 1f / dividerSize) * pos);
    }
	```
	* 循环轮播是往数据列表里塞进去窗口大小长度的数据个数，这些数据是重复，得到的ViewPager的ItemView就会多出窗口长度的个数，如原来是ABCDE，窗口长度是2，则向上轮播时填充数据后得到的View列表是ABCDEAB, 那么当ViewPager从头部AB一直滚到尾部AB时，使用viewPager.setCurrentItem(0，false) 直接回到顶部的AB处，接下来就会继续顺序滚动，看起来就是循环的
* 用法 
	```
    // 初始化VerticalRollView 泛型T继承Object的数据类和V继承于View的展示布局组件
    VerticalRollView<Bean, TextView> rollView = new VerticalRollView(); 
        rollView.setShowLength(showLength);   // 设置窗口展示View数量
        rollView.setDelayTime(3000);          // 设置轮播间隔
        rollView.setDuration(300);            // 设置pager切换动画时长
        rollView.setIViewGetter(beans, new IViewGetter<Bean, TextView>() {
            @Override
            public TextView createView(Context context) {
                return new TextView(context);    // 创建用于展示的view
            }
            
            @Override
            public void initView(TextView view, Bean bean) {
                view.setText(bean.text);         // 初始化view 
            }
        });
	 ```
* 效果
	https://github.com/peterbanban/VerticalRollView/blob/master/app/demo.gif
