package com.atguigu.beijingnews_library.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2017/6/7.
 */

public class BitmapCacheUtils {
    //网络缓存工具类
    private NetCachUtils netCachUtils;
    //本地缓存工具类
    private LocalCachUtils localCachUtils;

    public BitmapCacheUtils(Handler handler){

        localCachUtils = new LocalCachUtils();
        netCachUtils = new NetCachUtils(handler, localCachUtils);
    }

    /**
     * 三级缓存设计步骤：
       * 从内存中取图片
       * 从本地文件中取图片
            向内存中保持一份
       * 请求网络图片，获取图片，显示到控件上
          * 向内存存一份
          * 向本地文件中存一份

     * @param imageUrl
     * @param position
     * @return
     */
    public Bitmap getBitmap(String imageUrl, int position) {
        // 从内存中取图片

        //从本地文件中取图片
        if(localCachUtils != null) {
            Bitmap bitmap = localCachUtils.getBitmap(imageUrl);
            if(bitmap != null) {
                Log.e("TAG", "图片是从本地获取的==" + position);
                return bitmap;
            }
        }

        //请求网络图片，获取图片，显示到控件上
        netCachUtils.getBitmapFromNet(imageUrl,position);

        return null;
    }
}
