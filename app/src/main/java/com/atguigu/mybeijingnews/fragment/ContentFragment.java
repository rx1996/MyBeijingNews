package com.atguigu.mybeijingnews.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.activity.MainActivity;
import com.atguigu.mybeijingnews.base.BaseFragment;
import com.atguigu.mybeijingnews.base.BasePager;
import com.atguigu.mybeijingnews.pager.HomePager;
import com.atguigu.mybeijingnews.pager.NewsPager;
import com.atguigu.mybeijingnews.pager.SettingPager;
import com.atguigu.mybeijingnews.view.NoViewPager;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2017/6/2.
 */

public class ContentFragment extends BaseFragment {
    @InjectView(R.id.vp)
    NoViewPager vp;
    @InjectView(R.id.rg_main)
    RadioGroup rgMain;
    private TextView textView;

    private ArrayList<BasePager> pagers;

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_content, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //设ViewPager的数据-适配器
        //准备数据
        pagers = new ArrayList<>();
        pagers.add(new HomePager(context));//主页面
        pagers.add(new NewsPager(context));//新闻中心
        pagers.add(new SettingPager(context));//设置中心

        vp.setAdapter(new MyAdapter());
        //设置RadioGroup的监听
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_home:
                        vp.setCurrentItem(0,false);
                        break;
                    case R.id.rb_news:
                        vp.setCurrentItem(1,false);
                        break;
                    case R.id.rb_setting:
                        vp.setCurrentItem(2,false);
                        break;
                }
            }
        });
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //调用initData方法
                pagers.get(position).initData();
                if(position == 1) {
                    //可侧滑
                    isEnableSlidingMenu(context, SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else {
                    //不可侧滑
                    isEnableSlidingMenu((MainActivity) context, SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //默认选中主页
        rgMain.check(R.id.rb_home);
        //默认不可侧滑
        isEnableSlidingMenu((MainActivity) context, SlidingMenu.TOUCHMODE_NONE);
    }

    private static void isEnableSlidingMenu(Context context, int touchmodeFullscreen) {
        MainActivity ma = (MainActivity) context;
        ma.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }

    class MyAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = pagers.get(position);//HomePager,NewsPager,SettingPager
            View rootView = basePager.rootView;
            //调用initData方法
//            basePager.initData();//HomePager,NewsPager,SettingPager
            container.addView(rootView);
            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==object;
        }

        @Override
        public int getCount() {
            return pagers.size();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
