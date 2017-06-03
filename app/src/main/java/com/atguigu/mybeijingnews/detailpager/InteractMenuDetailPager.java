package com.atguigu.mybeijingnews.detailpager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mybeijingnews.base.MenuDetailBasePager;

/**
 * Created by Administrator on 2017/6/3.
 */

public class InteractMenuDetailPager extends MenuDetailBasePager {
    private TextView textView;
    public InteractMenuDetailPager(Context context) {
        super(context);
    }


    @Override
    public View initView() {
        //创建子类的视图
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        textView.setText("互动详情页面的内容");
    }
}
