package com.go.cqh.goodprojects.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.go.cqh.goodprojects.LoadMoreListener;
import com.go.cqh.goodprojects.R;
import com.go.cqh.goodprojects.view.WebViewActivity;
import com.go.cqh.goodprojects.adapter.ItemAdapter;
import com.go.cqh.goodprojects.base.BaseFragment;
import com.go.cqh.goodprojects.entry.ProjectsBean;
import com.go.cqh.goodprojects.utils.ConstantUtils;
import com.go.cqh.goodprojects.utils.ToastUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends BaseFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;

    //标签名
    private String tab;
    //是否刷新过
    private boolean isFromRefresh;
    //页码
    private int pagerNum;
    //每次加载多少条
    private int itemNum = 20;
    //请求队列
    private RequestQueue requestQueue;
    //条目适配器
    private ItemAdapter adapter;
    //下拉刷新请求码
    private static final int PULLUP_WHAT = 0x001;
    //上拉加载更多请求码
    private static final int PULLDOWN_WHAT = 0x002;
    //服务器拿到的数据
    public static List<ProjectsBean.ResultsBean> mList = new ArrayList<>();
    //handler中请求数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pagerNum = 1;
                    //格式化请求url
                    String path = String.format(ConstantUtils.PROJECT_TAB, tab, itemNum, pagerNum);
                    //创建请求
                    Request<JSONObject> request = NoHttp.createJsonObjectRequest(path, RequestMethod.GET);
                    //将请求添加到队列  NOHTTP_WHAT 这条请求的请求码
                    requestQueue.add(PULLUP_WHAT, request, onResponseListener);
                    break;
                case 1:
                    isFromRefresh = true;
                    handler.sendEmptyMessage(0);
                    break;
                //加载更多
                case 2:
                    pagerNum++;
                    //格式化请求url
                    String path2 = String.format(ConstantUtils.PROJECT_TAB, tab, itemNum, pagerNum);
                    //创建请求
                    Request<JSONObject> request2 = NoHttp.createJsonObjectRequest(path2, RequestMethod.GET);
                    //将请求添加到队列  PULLDOWN_WHAT 这条请求的请求码
                    requestQueue.add(PULLDOWN_WHAT, request2, onResponseListener);
                    break;
            }
        }
    };
    private boolean isPrepared;

    public AllFragment() {
    }

    public AllFragment(String tab) {
        this.tab = tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all, container, false);
        isPrepared = true;
        ButterKnife.bind(this, view);
        if (mList != null) {
            mList.clear();
        }
        requestQueue = NoHttp.newRequestQueue(3);
        //请求数据
        initRefresh();
        return view;
    }

    /**
     * 服务器请求相应
     */
    private OnResponseListener<JSONObject> onResponseListener = new OnResponseListener<JSONObject>() {

        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response response) {
            switch (what) {
                //下拉刷新
                case PULLUP_WHAT:
                    List<ProjectsBean.ResultsBean> refreshList;
                    try {
                        JSONObject jsonObject = (JSONObject) response.get();

                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        refreshList = JSON.parseArray(jsonArray.toString(), ProjectsBean.ResultsBean.class);
                        if (refreshList != null && refreshList.size() != 0) {
                            //数据加载完成，填充列表
                            mList.clear();
                            mList.addAll(refreshList);
                            initRecyclerView();
                        } else {
                            ToastUtils.showToast(getContext(),"没有相应数据");
                        }
                        if (isFromRefresh) {
                            refresh.setRefreshing(false);
                            showDropView("刷新成功");
                            isFromRefresh = !isFromRefresh;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                //上拉加载更多
                case PULLDOWN_WHAT:
                    List<ProjectsBean.ResultsBean> loadMoreList = null;
                    try {
                        JSONObject jsonObject = (JSONObject) response.get();
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        loadMoreList = JSON.parseArray(jsonArray.toString(), ProjectsBean.ResultsBean.class);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //加载完数据更新适配器
                    if (loadMoreList != null && loadMoreList.size() != 0) {
                        mList.addAll(loadMoreList);
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToast(getContext(),"没有更多数据");
                    }
                    break;
            }


        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            ToastUtils.showToast(getContext(), "无法连接网络，请稍后重试");
            if (isFromRefresh) {
                refresh.setRefreshing(false);
            }
        }

        @Override
        public void onFinish(int what) {

        }
    };

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ItemAdapter(getContext(), R.layout.item, mList);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", mList.get(position).url);
                intent.putExtra("desc", mList.get(position).desc);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.addOnScrollListener(new LoadMoreListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                handler.sendEmptyMessage(2);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化下拉刷新
     */
    private void initRefresh() {
        refresh.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
        refresh.setSize(SwipeRefreshLayout.DEFAULT);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 加载数据
     */
    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        handler.sendEmptyMessage(0);
    }
}
