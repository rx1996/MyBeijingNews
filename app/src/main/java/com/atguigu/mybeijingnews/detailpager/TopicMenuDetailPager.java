package com.atguigu.mybeijingnews.detailpager;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.activity.MainActivity;
import com.atguigu.mybeijingnews.base.MenuDetailBasePager;
import com.atguigu.mybeijingnews.domain.NewsCenterBean;
import com.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/3.
 */

public class TopicMenuDetailPager extends MenuDetailBasePager {
    //TabDetailPager的对应的数据
    private final List<NewsCenterBean.DataBean.ChildrenBean> datas;
    private ViewPager viewpager;
    private ImageButton ib_next;
    //TabDetailPager页面集合
    private List<TabDetailPager> tabDetailPagers;
    private TabLayout indicator;

    public TopicMenuDetailPager(Context context, List<NewsCenterBean.DataBean.ChildrenBean> children) {
        super(context);
        this.datas = children;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_topic_menu_detail,null);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        indicator = (TabLayout) view.findViewById(R.id.indicator);
        ib_next = (ImageButton) view.findViewById(R.id.ib_next);
        //设置点击事件
        ib_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换到下一个页面
                viewpager.setCurrentItem(viewpager.getCurrentItem()+1);
            }
        });
        //创建子类的视图

        //监听页面的改变
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    //SlidingMenu可以滑动
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else{
                    //不可以滑动
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        //根据数据创建子页面
        tabDetailPagers = new ArrayList<>();
        for(int i = 0; i < datas.size(); i++) {
            //一会再传递数据
            tabDetailPagers.add(new TabDetailPager(context,datas.get(i)));
        }

        //设置适配器
        viewpager.setAdapter(new NewsMenuDetailPagerAdapter());
        //TabPageIndicator和ViewPager关联起来
        indicator.setupWithViewPager(viewpager);
        indicator.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    class NewsMenuDetailPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return tabDetailPagers == null ? 0 : tabDetailPagers.size();
        }
        //得到标题
        @Override
        public CharSequence getPageTitle(int position) {
            return datas.get(position).getTitle();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            container.addView(rootView);
            tabDetailPager.initData();
            return rootView;
        }
    }
}
