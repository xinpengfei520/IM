package com.atguigu.im.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.atguigu.im.model.bean.GroupInfo;
import com.atguigu.im.model.bean.InvitationInfo;
import com.atguigu.im.model.bean.UserInfo;
import com.atguigu.im.utils.Constant;
import com.atguigu.im.utils.SPUtil;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by xpf on 2016/11/4 :)
 * Wechat:18091383534
 * Function:全局事件的监听类
 */

public class EventListener {

    private Context mContext;

    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;
        // 联系人全局监听
        EMClient.getInstance().contactManager().setContactListener(emContactLisener);

        // 群的全局监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);

        // 获取本地广播管理者的对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

    }

    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {

        // 收到群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            // 1 保存数据到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            // 封装群对象
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            // 邀请信息
            invitationInfo.setReason(reason);
            // 邀请状态
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //2.红点变化
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            //3.发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applicant, String reason) {

            //1.保存数据到本地数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION); //新申请

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //2.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            //3.发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, accepter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //2.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            // 3 发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {

            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));

            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            //3.发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            // 2 红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            // 3 发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {

            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //2.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE,true);

            //3.发送群邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            //2.红点的变化
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE,true);

            //3.发送群邀请信息的变化的广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
    };

    private final EMContactListener emContactLisener = new EMContactListener() {

        // 联系人增加了
        @Override
        public void onContactAdded(String hxid) {

            Log.e("TAG", "11111111111111111");
            // 本地
            Model.getInstance().getDbManager().getContactDao().getContactByHx(hxid);

            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 联系人删除了
        @Override
        public void onContactDeleted(String hxid) {

            Log.e("TAG", "222222222222222222");
            // 本地数据变化
            Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(hxid);

            // 发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 收到好友邀请
        @Override
        public void onContactInvited(String hxid, String reason) {

            Log.e("TAG", "3333333333333333333333333");
            // 1.本地数据变化
            InvitationInfo invitationInfo = new InvitationInfo();
            // 原因
            invitationInfo.setReason(reason);

            //联系人
            invitationInfo.setUser(new UserInfo(hxid));

            // 新邀请
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            // 2.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            //3.发送邀请广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

            Log.e("TAG", "777777777777777777777");
        }

        // 邀请被接受了
        @Override
        public void onContactAgreed(String hxid) {

            Log.e("TAG", "4444444444444444");
            // 1. 本地数据变化
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setUser(new UserInfo(hxid));
            //邀请被对方接受了
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);

            Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

            // 2.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            // 3.发送邀请广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 邀请被拒绝了
        @Override
        public void onContactRefused(String s) {
            Log.e("TAG", "555555555555555555555");

            //1.红点处理
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);

            // 2.发送邀请广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };
}
