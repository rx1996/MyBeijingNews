package com.atguigu.mybeijingnews.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.activity.MainActivity;
import com.atguigu.mybeijingnews.base.BaseFragment;
import com.atguigu.mybeijingnews.domain.NewsCenterBean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public class LeftMenuFragment extends BaseFragment {
    private ListView listView;
    private LeftMenuAdapter adapter;
    //传入的数据
    private List<NewsCenterBean.DataBean> datas;
    private int prePosition = 0;
    @Override
    public View initView() {
        listView = new ListView(context);
        listView.setPadding(0,40,0,0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //记录位置
                prePosition = position;
                //刷新适配器
                adapter.notifyDataSetChanged();//getCount-->getView

                MainActivity mainActivity = (MainActivity) context;
                mainActivity.getSlidingMenu().toggle();//关<->开

            }
        });
        return listView;
    }
    @Override
    public void initData() {
        super.initData();
    }
    public void setData(List<NewsCenterBean.DataBean> datas) {
        this.datas = datas;
        //设置适配器
        adapter = new LeftMenuAdapter();
        listView.setAdapter(adapter);

    }
    class LeftMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas ==null ? 0 : datas.size();
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
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);

            if(prePosition==position){
                //高亮
                textView.setEnabled(true);
            }else{
                //默认
                textView.setEnabled(false);
            }

            //根据位置得到数据
            NewsCenterBean.DataBean dataBean = datas.get(position);
            textView.setText(dataBean.getTitle());
            return textView;
        }
    }
}
