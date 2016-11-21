package com.go.cqh.goodprojects.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.go.cqh.goodprojects.R;
import com.go.cqh.goodprojects.base.BaseActivity;
import com.go.cqh.goodprojects.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.nestedscrollview)
    NestedScrollView nestedscrollview;
    @BindView(R.id.upBtn)
    FloatingActionButton upBtn;
    @BindView(R.id.root)
    CoordinatorLayout root;
    private String url;
    private String desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        //状态栏沉浸
        setStatusbarColor();
        nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(nestedscrollview.getScrollY()<=0) {
                    showUpBtn(false);
                    return;
                }
                int dis = scrollY - oldScrollY;
                if(dis>100)
                    showUpBtn(false);
                if(dis<0)
                    showUpBtn(true);

            }
        });
        url = getIntent().getStringExtra("url");
        desc = getIntent().getStringExtra("desc");
        progressBar.setMax(100);
        initToolBar();
        initWebView();

    }

    /**
     * 显示一键返回顶部的按扭
     * @param b 是否向下滑动 且滑动距离大于100
     */
    private void showUpBtn(boolean b) {
        upBtn.setVisibility(b?View.VISIBLE:View.GONE);
    }

    /**
     * 初始化 toolbar
     */
    private void initToolBar() {
        appbar.post(new Runnable() {
            @Override
            public void run() {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
                //params.topMargin =  mStatusBarHeight;
                appbar.setLayoutParams(params);
            }
        });
        toolbar.setTitle(desc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化 webview
     */
    private void initWebView() {
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView.loadUrl(url);
    }

    /**
     * 点击事件
     */
    @OnClick(R.id.upBtn)
    public void onClick() {
        nestedscrollview.fullScroll(View.FOCUS_UP);
    }
    /**
     * 创建toolbar菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view_menu, menu);
        //menu.findItem(R.id.action_collect).setTitle(isCollected?"取消收藏":"收藏");
        return true;
    }
    /**
     * toolbar 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //使用浏览器打开
        if (id == R.id.action_open) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
            return true;
        }
        //复制链接
        if (id == R.id.copyUrl) {
            //复制文字到粘贴板
            ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            myClipboard.setPrimaryClip(ClipData.newPlainText("url", url));
            ToastUtils.showToast(getApplicationContext(),"复制成功");
        }
        //复制链接
        if (id == R.id.shareWeb) {
            ToastUtils.showToast(getApplicationContext(),"复制成功");
        }
        //收藏
        if(id==R.id.action_collect){
            ToastUtils.showToast(getApplicationContext(),"开发中");
            //Collect collect = new Collect();
            //collect.setCollect_date(Calendar.getInstance().getTimeInMillis());
            //collect.setUrl(mUrl);
            //collect.setDesc(mDesc);
            //if(isCollected){
            //    DataManager.deleteCollect(collect,new SimpleSubscribers());
            //}else {
            //    DataManager.insertCollect(collect,new SimpleSubscribers());
            //}
            //isCollected = !isCollected;
            //item.setTitle(isCollected?"取消收藏":"收藏");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回键监听
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
