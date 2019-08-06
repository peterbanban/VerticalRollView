package com.peterbanban.verticalrollview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
import com.peterbanban.verticalrollview.VerticalRollView.IViewGetter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    VerticalRollView<Bean, TextView> mVerticalRollView1;
    VerticalRollView<Bean, TextView> mVerticalRollView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVerticalRollView1 = findViewById(R.id.roll_view_1);
        mVerticalRollView2 = findViewById(R.id.roll_view_2);
        initVerticalRollView(mVerticalRollView1, 1);
        initVerticalRollView(mVerticalRollView2, 3);
    }

    private void initVerticalRollView(VerticalRollView<Bean, TextView> rollView, int showLength){
        rollView.setShowLength(showLength);
        rollView.setDelayTime(3000);
        rollView.setIViewGetter(Bean.createBeanList(), new IViewGetter<Bean, TextView>() {
            @Override
            public TextView createView(Context context) {
                return new TextView(context);
            }

            @Override
            public void initView(TextView view, Bean bean) {
                view.setText(String.valueOf(bean.a));
                view.setGravity(Gravity.CENTER);
                if (bean.a % 2 == 0) {
                    view.setBackgroundResource(R.color.colorAccent);
                } else {
                    view.setBackgroundResource(R.color.colorPrimary);
                }
            }
        });
        rollView.startRoll();
    }

    static class Bean {
        int a;
        Bean(int data){
            a = data;
        }
        static List<Bean> createBeanList() {
            ArrayList<Bean> beans = new ArrayList<>();
            beans.add(new Bean(1));
            beans.add(new Bean(2));
            beans.add(new Bean(3));
            beans.add(new Bean(4));
            return beans;
        }
    }
}
