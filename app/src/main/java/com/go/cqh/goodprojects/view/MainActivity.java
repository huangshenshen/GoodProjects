package com.go.cqh.goodprojects.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.go.cqh.goodprojects.R;
import com.go.cqh.goodprojects.adapter.MyViewPagerAdapter;
import com.go.cqh.goodprojects.base.BaseActivity;
import com.go.cqh.goodprojects.fragments.AllFragment;
import com.go.cqh.goodprojects.utils.ToastUtils;
import com.go.cqh.goodprojects.utils.UIUtil;
import com.go.cqh.goodprojects.widget.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private List<String> mDataList;
    private List<Fragment> fragmentsList = new ArrayList<>();
    public static RelativeLayout relativeLayout;
    //记录第二次点击距离上次的时间间隔
    private long lastBackKeyDownTick = 0;
    //定义两次点击的间隔时长
    public static final long MAX_DOUBLE_BACK_DURATION = 1500;
    private String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        relativeLayout = ((RelativeLayout) findViewById(R.id.content_main));
        //状态栏沉浸
        setStatusbarColor();
        //获得tabs标签
        tabs = getResources().getStringArray(R.array.tabs);
        //转化为list
        mDataList = Arrays.asList(tabs);
        initData();
        initViewPager();
        setSupportActionBar(toolbar);
        initMagicIndicator();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    private void initData() {
        fragmentsList.clear();
        fragmentsList.add(new AllFragment("all"));
        fragmentsList.add(new AllFragment("Android"));
        fragmentsList.add(new AllFragment("iOS"));
        fragmentsList.add(new AllFragment("前端"));
        fragmentsList.add(new AllFragment("App"));
        fragmentsList.add(new AllFragment("休息视频"));
        fragmentsList.add(new AllFragment("福利"));
        fragmentsList.add(new AllFragment("拓展资源"));
        fragmentsList.add(new AllFragment("瞎推荐"));
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragmentsList));
    }

    /**
     * 初始化 指示器
     */
    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.5f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(i));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(Color.parseColor("#ffffff"));
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(i);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(5f));
                indicator.setYOffset(UIUtil.dip2px(context, 2));
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setColors(Color.parseColor("#ffffff"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    /**
     * 返回键 监听
     */
    @Override
    public void onBackPressed() {
        //关闭侧边栏
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        long currentTick = System.currentTimeMillis();
        if (currentTick - lastBackKeyDownTick > MAX_DOUBLE_BACK_DURATION) {
            ToastUtils.showToast(MainActivity.this, "再按一次退出");
            lastBackKeyDownTick = currentTick;
        } else {
            finish();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        }
    }

    /**
     * 创建toolbar菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * toolbar 菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ToastUtils.showToast(getApplicationContext(), "开发中");
            return true;
        }
        if (id == R.id.share) {
            ToastUtils.showToast(getApplicationContext(), "开发中");
            return true;
        }
        if (id == R.id.search) {
            ToastUtils.showToast(getApplicationContext(), "开发中");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧边栏点击事件
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
