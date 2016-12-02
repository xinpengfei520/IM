package com.atguigu.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.im.R;
import com.atguigu.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xpf on 2016/11/8 :)
 * Wechat:18091383534
 * Function:群详情页面的适配器
 */

public class GroupDetailAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsModify;             // 是否可以添加删除操作
    private boolean mIsDeleteMode = false; // 删除模式标记
    private List<UserInfo> mUsers = new ArrayList<>();
    private OnGroupDetailListener mOnGroupDetailListener; //群详情页面的监听器

    // 获取删除模式状态
    public boolean ismIsDeleteMode() {
        return mIsDeleteMode;
    }

    // 设置删除模式状态
    public void setmIsDeleteMode(boolean mIsDeleteMode) {
        this.mIsDeleteMode = mIsDeleteMode;
    }

    public GroupDetailAdapter(Context context, boolean isModify, OnGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsModify = isModify;
        mOnGroupDetailListener = onGroupDetailListener;
    }

    // 刷新方法
    public void refresh(List<UserInfo> users) {

        if (users != null && users.size() >= 0) {
            mUsers.clear();
            initUser(); // 添加加号和减号
            mUsers.addAll(0, users);
        }
        notifyDataSetChanged(); //刷新
    }

    private void initUser() {

        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");
        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1.创建获取viewholder
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_group_detail, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item数据
        final UserInfo userInfo = mUsers.get(position);
        // 显示数据
        if (mIsModify) { // 群主

            // 处理布局
            if (position == getCount() - 1) {// "-"号

                if (mIsDeleteMode) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }

            } else if (position == getCount() - 2) {// "+"号

                if (mIsDeleteMode) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.delete.setVisibility(View.GONE);
                    holder.name.setVisibility(View.INVISIBLE);
                }

            } else { // 联系人的位置
                // 显示控件
                convertView.setVisibility(View.VISIBLE);
                holder.name.setVisibility(View.VISIBLE);

                // 设置数据
                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.em_default_avatar);

                // 删除模式处理
                if (mIsDeleteMode) {
                    holder.delete.setVisibility(View.VISIBLE);
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }

            // 再处理点击事件
            if (position == getCount() - 1) { // "-"号
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteMode) { // 非删除模式
                            mIsDeleteMode = true;
                            notifyDataSetChanged(); // 刷新
                        }
                    }
                });
            } else if (position == getCount() - 2) { // "+"号

                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 添加群成员(接口回调)
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            } else { // 联系人的位置
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 删除群成员(接口回调)
                        mOnGroupDetailListener.onDeleteMember(userInfo);
                    }
                });
            }

        } else { // 群成员

            // 如果是加号或者减号直接隐藏
            if (position == getCount() - 1 || position == getCount() - 2) {
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.GONE);
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                holder.name.setText(userInfo.getName());
            }
        }
        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.iv_group_detail_photo)
        ImageView photo;
        @BindView(R.id.tv_group_detail_name)
        TextView name;
        @BindView(R.id.iv_group_detail_delete)
        ImageView delete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnGroupDetailListener {
        void onAddMembers();                // 添加群成员的方法

        void onDeleteMember(UserInfo user); // 删除群成员的方法
    }
}
