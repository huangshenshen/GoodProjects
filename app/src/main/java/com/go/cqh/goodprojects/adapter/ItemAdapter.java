package com.go.cqh.goodprojects.adapter;

import android.content.Context;
import android.view.View;

import com.go.cqh.goodprojects.R;
import com.go.cqh.goodprojects.entry.ProjectsBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by caoqianghui on 2016/11/17.
 */

public class ItemAdapter extends CommonAdapter<ProjectsBean.ResultsBean> {

    public ItemAdapter(Context context, int layoutId, List<ProjectsBean.ResultsBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, ProjectsBean.ResultsBean resultsBean, int position) {
        holder.setText(R.id.textView, resultsBean.desc);
    }
    @Override
    public void onViewHolderCreated(com.zhy.adapter.recyclerview.base.ViewHolder holder, View itemView) {
        super.onViewHolderCreated(holder, itemView);
        //适配listview
        AutoUtils.autoSize(itemView);
    }
}
