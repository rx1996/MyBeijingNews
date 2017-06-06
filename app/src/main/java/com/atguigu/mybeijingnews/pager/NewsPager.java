package com.atguigu.mybeijingnews.pager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.beijingnews_library.utils.CacheUtils;
import com.atguigu.beijingnews_library.utils.ConstantUtils;
import com.atguigu.mybeijingnews.activity.MainActivity;
import com.atguigu.mybeijingnews.base.BasePager;
import com.atguigu.mybeijingnews.base.MenuDetailBasePager;
import com.atguigu.mybeijingnews.detailpager.InteractMenuDetailPager;
import com.atguigu.mybeijingnews.detailpager.NewsMenuDetailPager;
import com.atguigu.mybeijingnews.detailpager.PhotosMenuDetailPager;
import com.atguigu.mybeijingnews.detailpager.TopicMenuDetailPager;
import com.atguigu.mybeijingnews.detailpager.VoteMenuDetailPager;
import com.atguigu.mybeijingnews.domain.NewsCenterBean;
import com.atguigu.mybeijingnews.fragment.LeftMenuFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/2.
 */

public class NewsPager extends BasePager {
    //左侧页面的数据集合
    private List<NewsCenterBean.DataBean> datas;
    //左侧菜单详情的页面集合
    private List<MenuDetailBasePager> basePagers;
    public NewsPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        //把数据绑定到视图上
        Log.e("TAG", "NewsPager-数据初始化...");
        //设置标题
        tv_title.setText("新闻");
        ib_menu.setVisibility(View.VISIBLE);

        //创建子类的视图
        TextView textView = new TextView(context);
        textView.setText("新闻页面的内容");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);

        //添加到布局上
        fl_content.addView(textView);
        //获取数据
        String saveJson = CacheUtils.getString(context, ConstantUtils.NEWSCENTER_PAGER_URL);
        if(!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
            Log.e("TAG","取出缓存的数据..=="+saveJson);
        }
        //联网请求
        getDataFromNet();
    }

    private void getDataFromNet() {
        //新闻中心的网络路径
        String url = ConstantUtils.NEWSCENTER_PAGER_URL;
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
                        Log.e("TAG", "请求成功==" + response);
                        //缓存数据
                        CacheUtils.putString(context,ConstantUtils.NEWSCENTER_PAGER_URL,response);
                        processData(response);
                    }
                });
    }
    private void processData(String json) {
//        NewsCenterBean newsCenterBean = new Gson().fromJson(json,NewsCenterBean.class);
        NewsCenterBean newsCenterBean = paraseJson(json);
        Log.e("TAG","解析成功了哦=="+ newsCenterBean.getData().get(0).getChildren().get(0).getTitle());
        datas = newsCenterBean.getData();
        //传到左侧菜单
        MainActivity mainActivity = (MainActivity) context;
        //得到左侧菜单Fragment
        LeftMenuFragment leftMenuFragment = mainActivity.getLeftMenuFragment();
        //设置数据
        leftMenuFragment.setData(datas);
        //实例化详情页面
        basePagers = new ArrayList<>();
        basePagers.add(new NewsMenuDetailPager(context,datas.get(0).getChildren()));//新闻详情页面
        basePagers.add(new TopicMenuDetailPager(context));//专题详情页面
        basePagers.add(new PhotosMenuDetailPager(context,datas.get(2)));//组图详情页面
        basePagers.add(new InteractMenuDetailPager(context));//互动详情页面
        basePagers.add(new VoteMenuDetailPager(context));//投票详情页面

        swichPager(0);
    }

    @NonNull
    private NewsCenterBean paraseJson(String json) {
        NewsCenterBean newsCenterBean = new NewsCenterBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int retcode = jsonObject.optInt("retcode");
            newsCenterBean.setRetcode(retcode);

            JSONArray jsonArray = jsonObject.optJSONArray("data");
            List<NewsCenterBean.DataBean> datas = new ArrayList<>();
            newsCenterBean.setData(datas);

            for(int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                if(jsonObject1 != null) {
                    NewsCenterBean.DataBean dataBean = new NewsCenterBean.DataBean();
                    datas.add(dataBean);

                    dataBean.setId(jsonObject1.optInt("id"));
                    dataBean.setTitle(jsonObject1.optString("title"));
                    dataBean.setType(jsonObject1.optInt("type"));
                    dataBean.setUrl(jsonObject1.optString("url"));

                    JSONArray children = jsonObject1.optJSONArray("children");
                    if(children != null) {
                        List<NewsCenterBean.DataBean.ChildrenBean> childrenBeans = new ArrayList<>();
                        dataBean.setChildren(childrenBeans);

                        for(int i1 = 0; i1 < children.length(); i1++) {
                            NewsCenterBean.DataBean.ChildrenBean  childrenBean = new NewsCenterBean.DataBean.ChildrenBean();
                            childrenBeans.add(childrenBean);
                            JSONObject childenObje = (JSONObject) children.get(i1);
                            childrenBean.setId(childenObje.optInt("id"));
                            childrenBean.setTitle(childenObje.optString("title"));
                            childrenBean.setType(childenObje.optInt("type"));
                            childrenBean.setUrl(childenObje.optString("url"));

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsCenterBean;
    }

    //根据位置切换到不同的详情页面
    public void swichPager(int prePosition) {
        
        //设置标题
        tv_title.setText(datas.get(prePosition).getTitle());

        MenuDetailBasePager basePager = basePagers.get(prePosition);//NewsMenuDetailPager,TopicMenuDetailPager...
        View rootView = basePager.rootView;
        fl_content.removeAllViews();//把之前显示的给移除

        fl_content.addView(rootView);

        //调用InitData
        basePager.initData();
        
        if(prePosition == 2 ) {
            //显示
            ib_switch_list_grid.setVisibility(View.VISIBLE);
            ib_switch_list_grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotosMenuDetailPager basePager1 = (PhotosMenuDetailPager) basePagers.get(2);
                    basePager1.swichListAndGrid(ib_switch_list_grid);
                }
            });
        }else {
            //隐藏
            ib_switch_list_grid.setVisibility(View.GONE);
        }

    }
}
