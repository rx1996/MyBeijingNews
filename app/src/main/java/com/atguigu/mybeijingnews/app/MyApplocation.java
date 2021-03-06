package com.atguigu.mybeijingnews.app;

import android.app.Application;

import com.atguigu.mybeijingnews.CrashHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/6/3.
 */

public class MyApplocation extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
        //极光推送初始化


        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        ImageLoaderConfiguration config = ImageLoaderConfiguration
                .createDefault(this);
        ImageLoader.getInstance().init(config);

        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());

    }
}
