package com.atguigu.mybeijingnews.detailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.beijingnews_library.utils.CacheUtils;
import com.atguigu.beijingnews_library.utils.ConstantUtils;
import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.activity.NewsDetailActivity;
import com.atguigu.mybeijingnews.base.MenuDetailBasePager;
import com.atguigu.mybeijingnews.domain.NewsCenterBean;
import com.atguigu.mybeijingnews.domain.TabDetailPagerBean;
import com.atguigu.mybeijingnews.view.HorizontalScrollViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/5.
 */

public class TabDetailPager extends MenuDetailBasePager {
    public static final String READ_ID_ARRAY = "read_id_array";
    private final NewsCenterBean.DataBean.ChildrenBean childrenBean;
    TextView tvTitle;
    LinearLayout llPointGroup;
    HorizontalScrollViewPager viewpager;
    @InjectView(R.id.pull_refresh_list)
    PullToRefreshListView pull_refresh_list;
    ListView lv;

    private int prePosition = 0;

    private String url;
    //顶部新闻的数据集合
    private List<TabDetailPagerBean.DataBean.TopnewsBean> topnews;
    //新闻列表数据集合
    private List<TabDetailPagerBean.DataBean.NewsBean> newsBeanList;
    private ListAdapter adapter;
    private String moreUrl;
    private boolean isLoadingMore = false;
    public TabDetailPager(Context context, NewsCenterBean.DataBean.ChildrenBean childrenBean) {
        super(context);
        this.childrenBean = childrenBean;
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.pager_tab_detail, null);
        ButterKnife.inject(this,view);
        //得到ListView
        lv = pull_refresh_list.getRefreshableView();
        /**
         * 下拉刷新添加声音
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        pull_refresh_list.setOnPullEventListener(soundListener);
        //顶部的视图
        View viewTopNews = View.inflate(context,R.layout.tab_detail_topnews,null);
        viewpager = (HorizontalScrollViewPager) viewTopNews.findViewById(R.id.viewpager);
        tvTitle = (TextView) viewTopNews.findViewById(R.id.tv_title);
        llPointGroup = (LinearLayout) viewTopNews.findViewById(R.id.ll_point_group);
        //把顶部的部分以添加头的方式加入ListView中
        lv.addHeaderView(viewTopNews);

        //创建子类的视图
        //设置监听ViewPager页面的变化
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //把之前的设置默认
                llPointGroup.getChildAt(prePosition).setEnabled(false);

                //当前的设置true
                llPointGroup.getChildAt(position).setEnabled(true);

                //记录当前值
                prePosition = position;
            }

            @Override
            public void onPageSelected(int position) {

                String title = topnews.get(position).getTitle();
                tvTitle.setText(title);




            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_DRAGGING) {
                    //消息移除
                    handler.removeCallbacksAndMessages(null);
                }else if(state == ViewPager.SCROLL_STATE_IDLE) {
                    //发消息
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(new MyRunnable(),4000);
                }
            }
        });
        //设置下拉和上拉刷新的监听
        pull_refresh_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isLoadingMore = false;
                getDataFromNet(url);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(!TextUtils.isEmpty(moreUrl)){
                    isLoadingMore = true;
                    getDataFromNet(moreUrl);
                }else{
                    Toast.makeText(context, "没有更多数据了...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //设置ListView的item的点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = position - 2;
                TabDetailPagerBean.DataBean.NewsBean newsBean = newsBeanList.get(realPosition);
                Log.e("TAG",""+newsBean.getId()+"-----------"+newsBean.getTitle());
                //获取
                String idArray = CacheUtils.getString(context, READ_ID_ARRAY);//""
                //判断是否存在-不存在
                if(!idArray.contains(newsBean.getId() + "")) {
                    idArray = idArray + newsBean.getId() + ",";
                    //保存
                    CacheUtils.putString(context,READ_ID_ARRAY,idArray);
                    adapter.notifyDataSetChanged();
                }
                String url = ConstantUtils.BASE_URL + newsBean.getUrl();

                //跳转到Activity显示新闻详情内容
                Intent intent = new Intent(context,NewsDetailActivity.class);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        url = ConstantUtils.BASE_URL + childrenBean.getUrl();
        Log.e("TAG","url=="+url);
        //设置数据
        getDataFromNet(url);
    }

    private void getDataFromNet(String url) {
        OkHttpUtils
                .get()
                .url(url)
//                .addParams("username", "hyman")
//                .addParams("password", "123")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("TAG", "请求失败==" + e.getMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        Log.e("TAG", "请求成功==" + response);
                        //缓存数据
                        processData(response);
                        //结束下来刷新
                        pull_refresh_list.onRefreshComplete();
                    }


                });
    }

    private InternalHandler handler;
    class InternalHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int item = (viewpager.getCurrentItem() + 1) % topnews.size();
            //设置切换到下一个页面
            viewpager.setCurrentItem(item);

            handler.postDelayed(new MyRunnable(),4000);
        }
    }
    class MyRunnable implements Runnable{

        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private void processData(String response) {
        TabDetailPagerBean bean = new Gson().fromJson(response,TabDetailPagerBean.class);
        String more = bean.getData().getMore();
        if(!TextUtils.isEmpty(more)){
            moreUrl = ConstantUtils.BASE_URL+more;
        }
        if(!isLoadingMore){
            //---------顶部--------------
            topnews = bean.getData().getTopnews();
            //设置适配器
            viewpager.setAdapter(new MyPagerAdapter());
            ;
            Log.e("TAG", "" + bean.getData().getNews().get(0).getTitle());

            tvTitle.setText(topnews.get(prePosition).getTitle());

            //把之前的移除
            llPointGroup.removeAllViews();
            //添加指示点
            for (int i = 0; i < topnews.size(); i++) {

                ImageView point = new ImageView(context);
                point.setBackgroundResource(R.drawable.point_selector);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
                point.setLayoutParams(params);

                if (i == 0) {
                    point.setEnabled(true);
                } else {
                    point.setEnabled(false);
                    params.leftMargin = 8;
                }
                //添加到线性布局
                llPointGroup.addView(point);
            }
            newsBeanList = bean.getData().getNews();
            adapter = new ListAdapter();
            lv.setAdapter(adapter);
        }else{
            isLoadingMore = false;
            //把新的数据集合加入到原来集合中，而不是覆盖
            newsBeanList.addAll(bean.getData().getNews());
            //适配器刷新
            adapter.notifyDataSetChanged();

        }
        //设置自动切换到下一个页面
        if(handler == null) {
            handler = new InternalHandler();
        }
        handler.postDelayed(new MyRunnable(),4000);

    }
    class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return newsBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = View.inflate(context,R.layout.item_tab_detail,null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TabDetailPagerBean.DataBean.NewsBean newsBean = newsBeanList.get(position);
            viewHolder.tvDesc.setText(newsBean.getTitle());
            viewHolder.tvTime.setText(newsBean.getPubdate());

            String imageUrl = ConstantUtils.BASE_URL + newsBean.getListimage();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.pic_item_list_default)
                    .error(R.drawable.pic_item_list_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.ivIcon);

            //判断是否已经被点击
            String idArray  = CacheUtils.getString(context,READ_ID_ARRAY);
            if(idArray.contains(newsBean.getId()+"")){
                //灰色
                viewHolder.tvDesc.setTextColor(Color.GRAY);
            }else{
                //黑色
                viewHolder.tvDesc.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }
    static class ViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView ivIcon;
        @InjectView(R.id.tv_desc)
        TextView tvDesc;
        @InjectView(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //创建图片
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.pic_item_list_default);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //设置网络图片
            String imageUrl = ConstantUtils.BASE_URL + topnews.get(position).getTopimage();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.pic_item_list_default)
                    .error(R.drawable.pic_item_list_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            container.addView(imageView);
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        //按下的时候移除消息
                        case MotionEvent.ACTION_DOWN :
                            handler.removeCallbacksAndMessages(null);
                            Log.e("TAG","onTouch--ACTION_DOWN==");
                            break;
                        //离开的时候重新发消息
                        case MotionEvent.ACTION_UP:
                            handler.postDelayed(new MyRunnable(),4000);
                            Log.e("TAG","--onTouch--ACTION_UP==");
                            break;
                    }
                    return true;
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
