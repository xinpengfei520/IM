package com.xpf.im.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.xpf.im.R;
import com.xpf.im.utils.Constant;

/**
 * 会话详情页面
 */
public class ChatActivity extends FragmentActivity {

    private EaseChatFragment easeChatFragment;
    private LocalBroadcastManager mLBM;
    private String mHxid;
    private int mChatType;

    // 消息列表条目的点击事件的监听器
    EaseChatMessageList.MessageListItemClickListener messageListItemClickListener = new EaseChatMessageList.MessageListItemClickListener() {
        @Override
        public void onResendClick(EMMessage message) {
        }

        @Override
        public boolean onBubbleClick(EMMessage message) {

            if (message.getType() == EMMessage.Type.IMAGE && message.status().equals(EMMessage.Status.SUCCESS)) {
                Intent intent = new Intent(ChatActivity.this, PhotoDetailsActivity.class);
                intent.putExtra("photo", message);
                startActivity(intent);
            }

            return true;
        }

        @Override
        public void onBubbleLongClick(EMMessage message) {

        }

        @Override
        public void onUserAvatarClick(String username) {

        }

        @Override
        public void onUserAvatarLongClick(String username) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();     // 初始化数据
        initListener(); // 初始化监听
    }

    private void initListener() {


        easeChatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {

            @Override
            public void onSetMessageAttributes(EMMessage message) {
            }

            @Override
            public void onEnterToChatDetails() {
                Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                intent.putExtra(Constant.GROUP_ID, mHxid);// 群id
                startActivity(intent);
            }

            @Override
            public void onAvatarClick(String username) {
            }

            @Override
            public void onAvatarLongClick(String username) {
            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                messageListItemClickListener.onBubbleClick(message);
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {
            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });

        // 如果当前是群聊我再注册监听
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {

            //注册退群广播
            BroadcastReceiver ExitGroupReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mHxid.equals(intent.getStringExtra(Constant.GROUP_ID))) {
                        // 结束当前页面
                        finish();
                    }
                }
            };
            mLBM.registerReceiver(ExitGroupReceiver, new IntentFilter(Constant.EXIT_GROUP));
        }
    }

    private void initData() {

        // 创建一个会话的fragment
        easeChatFragment = new EaseChatFragment();
        // 获取传递过来的环信id
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        // 获取聊天类型
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        // 设置传递过来的参数
        easeChatFragment.setArguments(getIntent().getExtras());

        // 替换Fragment
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_chat_detail, easeChatFragment)
                .commit();

        // 获取本地发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(ChatActivity.this);
    }

}
