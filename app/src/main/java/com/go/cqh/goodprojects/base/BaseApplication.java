package com.go.cqh.goodprojects.base;

import android.app.Activity;
import android.app.Application;

import com.tencent.bugly.Bugly;
import com.yolanda.nohttp.NoHttp;

import java.util.List;

/**
 * Created by caoqianghui on 2016/11/17.
 */

public class BaseApplication extends Application {
    private List<Activity> activitys;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化NoHttp
        NoHttp.initialize(this);
        //设置全局默认超时时间
        NoHttp.setDefaultConnectTimeout(10000);
        NoHttp.setDefaultReadTimeout(10000);
        /**
         * 初始化bugly
         * 参数1：上下文对象
         * 参数2：注册时申请的APPID
         * 参数3：是否开启debug模式，true表示打开debug模式，false表示关闭调试模式
         */
        Bugly.init(getApplicationContext(), "de5e809256", false);
    }
}
