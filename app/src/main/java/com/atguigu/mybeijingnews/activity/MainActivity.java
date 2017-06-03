package com.atguigu.mybeijingnews.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.fragment.ContentFragment;
import com.atguigu.mybeijingnews.fragment.LeftMenuFragment;
import com.atguigu.mybeijingnews.utils.DensityUtil;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {


    public static final String LEFT_TAG = "LEFT_TAG";
    public static final String MAIN_TAG = "MAIN_TAG";

    //设置主页面
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置左侧菜单
        setBehindContentView(R.layout.left_menu);

        SlidingMenu slidingMenu = getSlidingMenu();
        //设置右侧菜单
//        slidingMenu.setSecondaryMenu(R.layout.left_menu);
        //设置模式：左侧+主页；左侧+主页+右侧；主页+右侧
        slidingMenu.setMode(SlidingMenu.LEFT);
        //设置滑动模式：不可用滑动，滑动编边缘，全屏滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //设置主页面占的宽度
        slidingMenu.setBehindOffset(DensityUtil.dip2px(MainActivity.this,200));

        initFragment();
    }

    private void initFragment() {
        //得到FragmentManger
        FragmentManager fm = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //替换两个Fragment
        ft.replace(R.id.fl_left,new LeftMenuFragment(), LEFT_TAG);
        ft.replace(R.id.fl_main,new ContentFragment(), MAIN_TAG);
        //提交事物
        ft.commit();
    }
    /**
     * 得到左侧菜单Fragment
     * @return
     */
    public LeftMenuFragment getLeftMenuFragment() {
        return  (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(LEFT_TAG);
    }
}
