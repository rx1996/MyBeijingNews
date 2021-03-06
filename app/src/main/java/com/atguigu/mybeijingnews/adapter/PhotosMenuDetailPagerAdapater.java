package com.atguigu.mybeijingnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.beijingnews_library.utils.BitmapCacheUtils;
import com.atguigu.beijingnews_library.utils.ConstantUtils;
import com.atguigu.beijingnews_library.utils.NetCachUtils;
import com.atguigu.mybeijingnews.R;
import com.atguigu.mybeijingnews.activity.PicassoSampleActivity;
import com.atguigu.mybeijingnews.domain.PhotosMenuDetailPagerBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.atguigu.mybeijingnews.R.id.recyclerview;

/**
 * Created by Administrator on 2017/6/6.
 */

public class PhotosMenuDetailPagerAdapater extends RecyclerView.Adapter<PhotosMenuDetailPagerAdapater.MyViewHolder> {


    private final List<PhotosMenuDetailPagerBean.DataBean.NewsBean> datas;
    private final Context context;
    private final RecyclerView recyclerview;
    private BitmapCacheUtils bitmapCacheUtils;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NetCachUtils.SUCESS:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int position = msg.arg1;
                    Log.e("TAG","请求图片成功=="+position);
                    ImageView imageview = (ImageView) recyclerview.findViewWithTag(position);
                    if(imageview != null && bitmap != null){
                        imageview.setImageBitmap(bitmap);
                    }


                    break;
                case NetCachUtils.FAIL:
                    position = msg.arg1;
                    Log.e("TAG","请求图片失败=="+position);
                    break;
            }
        }
    };


    public PhotosMenuDetailPagerAdapater(Context context, List<PhotosMenuDetailPagerBean.DataBean.NewsBean> datas, RecyclerView recyclerview) {
        this.datas = datas;
        this.context = context;
        //把Hanlder传入构造方法
        bitmapCacheUtils = new BitmapCacheUtils(handler);
        this.recyclerview = recyclerview;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_photos, null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //1.根据位置得到对应的数据
        PhotosMenuDetailPagerBean.DataBean.NewsBean newsBean = datas.get(position);
        //2.绑定数据
        holder.tvTitle.setText(newsBean.getTitle());
        //3.设置点击事件
        String imageUrl = ConstantUtils.BASE_URL + newsBean.getListimage();
        //使用Glide请求图片
//        Glide.with(context)
//                .load(imageUrl)
//                .placeholder(R.drawable.pic_item_list_default)
//                .error(R.drawable.pic_item_list_default)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.ivIcon);
        //使用自定义方式请求图片
//        Bitmap bitmap = bitmapCacheUtils.getBitmap(imageUrl,position);
//        //图片对应的Tag就是位置
//        holder.ivIcon.setTag(position);
//        if(bitmap != null){//来自内存和本地，不包括网络
//            holder.ivIcon.setImageBitmap(bitmap);
//        }


        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_item_list_default)
                .showImageForEmptyUri(R.drawable.pic_item_list_default)
                .showImageOnFail(R.drawable.pic_item_list_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.ivIcon,options);
    }

    /**
     * 返回总大小
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.iv_icon)
        ImageView ivIcon;
        @InjectView(R.id.tv_title)
        TextView tvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            //设置RecyclerView的item的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //跳转到显示图片的Activity页面，使用的PhotoView
                    String imageUrl = ConstantUtils.BASE_URL + datas.get(getLayoutPosition()).getListimage();
                    Intent intent = new Intent(context, PicassoSampleActivity.class);
                    intent.setData(Uri.parse(imageUrl));
                    context.startActivity(intent);

                }
            });
        }
    }
}

