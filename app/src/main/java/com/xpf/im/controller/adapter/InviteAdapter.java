package com.xpf.im.controller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xpf.im.R;
import com.xpf.im.model.bean.InvitationInfo;
import com.xpf.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xpf on 2016/11/4 :)
 * Wechat:18091383534
 * Function:邀请信息的适配器
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvitationInfo> mInvitationInfos = new ArrayList<>();

    // 2.定义接口变量
    private OnInviteChangedListener mOnInviteChangedListener;

    public InviteAdapter(Context context, OnInviteChangedListener onInviteChangedListener) {
        mContext = context;

        // 3.接收传过来的接口
        mOnInviteChangedListener = onInviteChangedListener;
    }

    // 获取邀请信息数据
    public void refresh(List<InvitationInfo> invitationInfos) {

        if (invitationInfos != null && invitationInfos.size() > 0) {
            // 获取数据
            mInvitationInfos.clear();
            mInvitationInfos.addAll(invitationInfos);
            Log.e("siuze", "" + invitationInfos.size());
            // 页面变化刷新
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitationInfos == null ? 0 : mInvitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1.获取或创建viewHolder
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_invite, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //2.获取当前item数据
        final InvitationInfo invitationInfo = mInvitationInfos.get(position);

        // 3.设置显示数据
        UserInfo user = invitationInfo.getUser();

        if (user == null) { // 群邀请信息

            //名称
            holder.name.setText(invitationInfo.getGroup().getInvatePerson());

            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);
            //原因
            switch (invitationInfo.getStatus()) {
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.reason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.reason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.reason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.reason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.reason.setText("您收到了群邀请");

                    //显示接受和拒绝按钮
                    holder.btn_accept.setVisibility(View.VISIBLE);
                    holder.btn_reject.setVisibility(View.VISIBLE);

                    //群邀请信息接受按钮的点击事件的处理
                    holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteChangedListener.onAcceptInvite(invitationInfo);
                        }
                    });

                    // 群邀请信息的拒绝按钮处理
                    holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteChangedListener.onRejectInvite(invitationInfo);
                        }
                    });
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:

                    holder.reason.setText("您收到了群申请");

                    //显示接受和拒绝按钮
                    holder.btn_accept.setVisibility(View.VISIBLE);
                    holder.btn_reject.setVisibility(View.VISIBLE);

                    //群邀请信息接受按钮的点击事件的处理
                    holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteChangedListener.onAcceptApplication(invitationInfo);
                        }
                    });

                    // 群邀请信息的拒绝按钮处理
                    holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteChangedListener.onRejectApplication(invitationInfo);
                        }
                    });
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.reason.setText("你接受了群邀请");
                    break;

                // 您接受了群申请
                case GROUP_ACCEPT_APPLICATION:
                    holder.reason.setText("您接受了群申请");
                    break;

                // 你拒绝了群邀请
                case GROUP_REJECT_INVITE:
                    holder.reason.setText("你拒绝了群邀请");
                    break;

                // 您拒绝了群申请
                case GROUP_REJECT_APPLICATION:
                    holder.reason.setText("您拒绝了群申请");
                    break;
            }

        } else { // 好友邀请信息

            holder.name.setText(user.getName());

            // 控制接受按钮和拒绝按钮的隐藏和显示
            holder.btn_accept.setVisibility(View.GONE);
            holder.btn_reject.setVisibility(View.GONE);

            // 当有新邀请
            if (InvitationInfo.InvitationStatus.NEW_INVITE == invitationInfo.getStatus()) {
                if (invitationInfo.getReason() != null) {
                    holder.reason.setText(invitationInfo.getReason());
                } else {
                    holder.reason.setText("好友邀请");
                }

                // 显示接受按钮和拒绝按钮
                holder.btn_accept.setVisibility(View.VISIBLE);
                holder.btn_reject.setVisibility(View.VISIBLE);

            } else if (InvitationInfo.InvitationStatus.INVITE_ACCEPT == invitationInfo.getStatus()) { // 邀请被接受
                if (invitationInfo.getReason() != null) {
                    holder.reason.setText(invitationInfo.getReason());
                } else {
                    holder.reason.setText("接受邀请");
                }

            } else if (InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER == invitationInfo.getStatus()) { // 邀请被拒绝
                if (invitationInfo.getReason() != null) {
                    holder.reason.setText(invitationInfo.getReason());
                } else {
                    holder.reason.setText("邀请被接受");
                }
            }

            // 设置button的监听(接收)
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 4.具体的放置方法
                    mOnInviteChangedListener.onAccept(invitationInfo);
                }
            });

            // 设置button的监听(拒绝)
            holder.btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteChangedListener.onReject(invitationInfo);
                }
            });

        }

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.tv_invite_name)
        TextView name; // 名称

        @BindView(R.id.tv_invite_reason)
        TextView reason; // 原因

        @BindView(R.id.btn_invite_accept)
        Button btn_accept; // 接收按钮

        @BindView(R.id.btn_invite_reject)
        Button btn_reject; // 拒绝按钮

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 定义一个接口(用来被实现的)
     */
    public interface OnInviteChangedListener {
        //接受好友邀请
        void onAccept(InvitationInfo invitationInfo);

        //拒绝好友邀请
        void onReject(InvitationInfo invitationInfo);

        // 接受群邀请
        void onAcceptInvite(InvitationInfo invitationInfo);

        // 拒绝群邀请
        void onRejectInvite(InvitationInfo invitationInfo);

        // 接受群申请
        void onAcceptApplication(InvitationInfo invitationInfo);

        // 拒绝群申请
        void onRejectApplication(InvitationInfo invitationInfo);
    }
}
