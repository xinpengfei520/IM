package com.atguigu.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atguigu.im.ImApplication;
import com.atguigu.im.R;
import com.atguigu.im.controller.activity.AddContactActivity;
import com.atguigu.im.controller.activity.ChatActivity;
import com.atguigu.im.controller.activity.GroupListActivity;
import com.atguigu.im.controller.activity.InviteActivity;
import com.atguigu.im.model.Model;
import com.atguigu.im.model.bean.UserInfo;
import com.atguigu.im.utils.Constant;
import com.atguigu.im.utils.SPUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:
 */

public class ContactListFragment extends EaseContactListFragment {

    private EaseUser easeUser;
    private ImageView iv_contactlist_red; // 红点

    /**
     * 好友邀请信息条目对象
     */
    private LinearLayout ll_contactlist_invite;

    /**
     * 群组邀请信息条目对象
     */
    private LinearLayout ll_contactlist_group;

    /**
     * 本地广播的管理者对象
     */
    private LocalBroadcastManager mLBM;

    // 创建一个邀请信息变化的广播接收器对象
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TAG", "6666666666666666666666");

            // 红点显示
            SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, true);
            iv_contactlist_red.setVisibility(View.VISIBLE);
        }
    };

    // 创建一个联系人信息变化的广播接收器对象
    private BroadcastReceiver ContactChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新页面
            contactRefresh();
        }
    };

    @Override
    protected void initView() {
        super.initView();

        // 添加好友
        titleBar.setRightImageResource(R.drawable.addfriends);

        // 添加头布局
        View headerview = View.inflate(getActivity(), R.layout.header_contactlist, null);

        // 获取红点对象
        iv_contactlist_red = (ImageView) headerview.findViewById(R.id.iv_contactlist_red);

        // 好友邀请信息条目对象
        ll_contactlist_invite = (LinearLayout) headerview.findViewById(R.id.ll_contactlist_invite);

        // 群组邀请信息条目对象
        ll_contactlist_group = (LinearLayout) headerview.findViewById(R.id.ll_contactlist_group);

        listView.addHeaderView(headerview);

        // 监听联系人条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                // 校验
                if (user == null) {
                    return;
                }

                // 跳转到会话详情页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                // 传递参数，带回调参数的启动
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());

                startActivity(intent);
            }
        });

        // 群组条目的点击事件
        ll_contactlist_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到群组列表页面
                startActivity(new Intent(getActivity(), GroupListActivity.class));
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加联系人页面
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });

        //获取红点状态并显示
        boolean isNewInvite = SPUtil.getInstace().getBoolean(SPUtil.IS_NEW_INVITE, false);
        iv_contactlist_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        // 邀请信息条目点击事件处理
        ll_contactlist_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.隐藏红点
                SPUtil.getInstace().save(SPUtil.IS_NEW_INVITE, false);
                iv_contactlist_red.setVisibility(View.GONE);

                // 2.跳转
                startActivity(new Intent(getActivity(), InviteActivity.class));
            }
        });

        // 监听广播
        mLBM = LocalBroadcastManager.getInstance(ImApplication.getContext());
        // 联系人邀请信息变化的广播注册
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 联系人信息变化的广播注册
        mLBM.registerReceiver(ContactChangedReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        // 群邀请信息变化的广播注册
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));

        // 从环信服务器获取联系人信息
        getContactFromHxServer();

        // 1.注册(listView从父类中继承而来)
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 1.获取当前item数据
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        easeUser = (EaseUser) listView.getItemAtPosition(position);
        // 2.加载布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // 3.点击事件处理
        if (item.getItemId() == R.id.delete) {
            deleteContact();

            return true;
        }
        return super.onContextItemSelected(item);
    }

    // 删除联系人
    private void deleteContact() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 网络删除联系人
                    EMClient.getInstance().contactManager().deleteContact(easeUser.getUsername());

                    // 本地
                    Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(easeUser.getUsername());

                    if (getActivity() == null) {
                        return;
                    }

                    // 内存和页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + easeUser.getUsername() + "成功", Toast.LENGTH_SHORT).show();
                            // 刷新页面
                            contactRefresh();
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + easeUser.getUsername() + "失败!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    /**
     * 从环信服务器获取联系人信息
     */
    private void getContactFromHxServer() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 网络获取所有联系人环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                    if (hxids != null && hxids.size() > 0) {
                        List<UserInfo> users = new ArrayList<UserInfo>();

                        //转化
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            users.add(userInfo);
                        }

                        // 本地
                        Model.getInstance().getDbManager().getContactDao().saveContact(users, true);

                        // 内存和页面
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contactList.remove(easeUser);
                                // 刷新页面
                                contactRefresh();
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 刷新页面的方法
    private void contactRefresh() {

        // 获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactDao().getContacts();

        // 转化
        if (contacts != null && contacts.size() >= 0) {

            Map<String, EaseUser> contactsMap = new HashMap<>();

            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactsMap.put(contact.getHxid(), easeUser);
            }

            // 通知listView刷新
            // 设置数据
            setContactsMap(contactsMap);

            // 通知刷新
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 一定要解注册,否则会出现内存泄漏
        mLBM.unregisterReceiver(InviteChangedReceiver);
        mLBM.unregisterReceiver(ContactChangedReceiver);
    }
}
