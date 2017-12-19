package com.xpf.im.controller.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.xpf.im.R;
import com.xpf.im.controller.activity.AddContactActivity;
import com.xpf.im.controller.activity.ChatActivity;
import com.xpf.im.controller.activity.GroupListActivity;
import com.xpf.im.controller.activity.ScanResultActivity;
import com.xpf.im.utils.DensityUtil;

import java.util.List;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:会话列表页面
 */

public class ConversationFragment extends EaseConversationListFragment implements View.OnClickListener {

    // 声明PopupWindow
    private PopupWindow popupWindow;
    // 声明PopupWindow对应的视图
    private View popupView;

    // 声明缩放动画
    private ScaleAnimation scaleAnimation;

    @Override
    protected void initView() {
        super.initView();

        // 添加加号
        titleBar.setRightImageResource(R.drawable.em_add);

        // 会话条目的点击事件
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {

                //跳到会话详情页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());

                // 会话类型
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });

        // 显示会话列表之前清空会话列表
        conversationList.clear();

        // 注册会话列表的监听
        EMClient.getInstance().chatManager().addMessageListener(emMessageLitener);
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupWindow(v);
            }
        });
    }

    private void showPopupWindow(View v) {

        if (popupWindow == null) {
            popupView = View.inflate(getActivity(), R.layout.item_popupwindow, null);
            // 参数2,3：指明popupwindow的宽度和高度
            popupWindow = new PopupWindow(popupView, DensityUtil.px2dip(getContext(), 200), DensityUtil.px2dip(getContext(), 225));

            // 设置背景图片， 必须设置，不然动画没作用
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            // 设置点击popupwindow外屏幕其它地方消失
            popupWindow.setOutsideTouchable(true);

            // 设置缩放动画(默认从右上角开始)
            scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
            scaleAnimation.setDuration(500);

            //通过popupView获取其内部的三个Linearlayout,并分别设置监听
            popupView.findViewById(R.id.tv_group_chat).setOnClickListener(this);
            popupView.findViewById(R.id.tv_add_friend).setOnClickListener(this);
            popupView.findViewById(R.id.tv_scan).setOnClickListener(this);
        }

        //在重新显示之前，设置popupwindow的销毁
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

        //显示
        popupWindow.showAsDropDown(v, 120, 10);

        //设置动画
        popupView.startAnimation(scaleAnimation);
    }

    private EMMessageListener emMessageLitener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {

            //获取数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);

            // 刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_group_chat:
                // 跳转到添加联系人页面
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.tv_add_friend:
                // 跳转到添加联系人页面
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                break;
            case R.id.tv_scan:
                startActivity(new Intent(getActivity(), ScanResultActivity.class));
                break;
        }
        popupWindow.dismiss();
    }

}
