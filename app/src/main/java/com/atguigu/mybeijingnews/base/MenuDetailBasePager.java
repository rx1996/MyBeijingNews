package com.atguigu.mybeijingnews.base;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/6/3.
 */

public abstract class MenuDetailBasePager {

    public final Context context;
    public View rootView;

    public MenuDetailBasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    //该方法是抽象的，由子类实现，达到自己的视图
    public abstract View initView();

    /**
     * 子类要绑定数据的时候重写该方法
     */
    public void initData(){

    }
}
