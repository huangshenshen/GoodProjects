package com.go.cqh.goodprojects.base;


import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.go.cqh.goodprojects.utils.BarUtil;
import com.zhy.autolayout.AutoLayoutActivity;

/**
 * Created by caoqianghui on 2016/11/17.
 */

public class BaseActivity extends AutoLayoutActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    //状态栏沉浸
    public void setStatusbarColor() {
        BarUtil.setColor(this, Color.parseColor("#009688"));
    }
}
