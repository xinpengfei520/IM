package com.atguigu.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.im.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xpf on 2016/11/5 :)
 * Wechat:18091383534
 * Function:群邀请的适配器
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> emGroups = new ArrayList<>();

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    // 刷新
    public void refresh(List<EMGroup> groups) {
        emGroups.clear();

        emGroups.addAll(groups);

        // 刷新页面
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return emGroups == null ? 0 : emGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return emGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取或创建viewholder
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_grouplist, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //获取当前item数据
        EMGroup emGroup = emGroups.get(position);

        // 显示数据
        holder.name.setText(emGroup.getGroupName());

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.tv_group_name)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
