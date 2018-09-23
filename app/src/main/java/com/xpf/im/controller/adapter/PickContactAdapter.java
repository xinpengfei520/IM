package com.xpf.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xpf.im.R;
import com.xpf.im.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xpf on 2016/11/7 :)
 * Wechat:18091383534
 * Function:选择联系人的页面适配器
 */

public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>(); // 选择的联系人集合
    private List<String> mExistMembers = new ArrayList<>();   // 群中存在的联系人集合(群成员)

    public PickContactAdapter(Context context, List<PickContactInfo> pickContacts, List<String> existMembers) {
        mContext = context;

        // 获取数据
        if (pickContacts != null && pickContacts.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(pickContacts);
        }

        // 获取已经存在的群成员信息
        if (existMembers != null && existMembers.size() >= 0) {
            mExistMembers.clear();
            mExistMembers.addAll(existMembers);
        }
    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 创建获取ViewHolder
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_pickcontact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        // 显示数据
        holder.cb.setChecked(pickContactInfo.isChecked());
        holder.name.setText(pickContactInfo.getUser().getName());

        // 如果mExistMembers集合中存在当前的群成员就选中
        if (mExistMembers != null && mExistMembers.contains(pickContactInfo.getUser().getHxid())) {
            holder.cb.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.cb_pickcontact)
        CheckBox cb;
        @BindView(R.id.tv_pickcontact_name)
        TextView name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view); // 初始化
        }
    }

    /**
     * 获取选择的联系人集合
     */
    public List<String> getPickContacts() {

        List<String> picks = new ArrayList<>();
        // 循环查找被选择的联系人
        if (mPicks != null && mPicks.size() >= 0) {
            for (PickContactInfo pick : mPicks) {
                if (pick.isChecked()) { // 判断是否被选中了
                    picks.add(pick.getUser().getHxid());
                }
            }
        }
        return picks;
    }
}
