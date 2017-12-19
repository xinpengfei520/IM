package com.atguigu.im.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.atguigu.im.ImApplication;
import com.atguigu.im.R;
import com.atguigu.im.controller.adapter.InviteAdapter;
import com.atguigu.im.model.Model;
import com.atguigu.im.model.bean.InvitationInfo;
import com.atguigu.im.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 邀请信息页面
 */
public class InviteActivity extends Activity {

    @BindView(R.id.lv_invite)
    ListView lvInvite;

    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh(); // 刷新页面
        }
    };

    /**
     * 邀请信息变化的监听器(接口回调)
     */
    private InviteAdapter.OnInviteChangedListener mOnInviteChangedListener = new InviteAdapter.OnInviteChangedListener() {

        // 接受按钮处理
        @Override
        public void onAccept(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    // 网络
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUser().getHxid());

                        // 本地
                        Model.getInstance().getDbManager().getInvitationDao()
                                .updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT, invitationInfo.getUser().getHxid());

                        // 内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新页面
                                refresh();
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 拒绝按钮处理
        @Override
        public void onReject(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 网络
                        EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUser().getHxid());

                        // 本地数据变化
                        // 删除和这个人的所有邀请新信息
                        Model.getInstance().getDbManager().getInvitationDao().removeInvitation(invitationInfo.getUser().getHxid());
                        // 删除这个人
                        Model.getInstance().getDbManager().getContactDao().deleteContactByHxId(invitationInfo.getUser().getHxid());

                        // 内存和页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新
                                refresh();

                                // 页面
                                Toast.makeText(InviteActivity.this, "拒绝成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 接受群邀请
        @Override
        public void onAcceptInvite(final InvitationInfo invitationInfo) {
            //联网
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    //接受消息后发送给环信服务器
                    try {
                        EMClient.getInstance().groupManager()
                                .acceptInvitation(invitationInfo.getGroup().getGroupId(),
                                        invitationInfo.getGroup().getInvatePerson());

                        //本地数据变化
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);//接受群邀请

                        Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

                        //内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了邀请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 拒绝群邀请
        @Override
        public void onRejectInvite(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 把状态发送给环信服务器
                        EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(),
                                invitationInfo.getGroup().getInvatePerson(), "对方拒绝了你");

                        //本地数据的变化
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE);

                        Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

                        //内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了邀请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝群申请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 接受群申请
        @Override
        public void onAcceptApplication(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 网络数据变化
                        EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvatePerson());

                        //本地数据变化
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

                        //内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受了群申请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受群申请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        // 拒绝群申请
        @Override
        public void onRejectApplication(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 网络数据变化
                        EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvatePerson(), "拒绝了你的申请");

                        //本地数据变化
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getInstance().getDbManager().getInvitationDao().addInvitation(invitationInfo);

                        //内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝了群申请", Toast.LENGTH_SHORT).show();

                                //刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝群申请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        // 初始化listView
        inviteAdapter = new InviteAdapter(this, mOnInviteChangedListener);

        lvInvite.setAdapter(inviteAdapter);

        // 刷新
        refresh();

        // 注册广播
        mLBM = LocalBroadcastManager.getInstance(ImApplication.getContext());
        // 联系人邀请信息变化的广播注册
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        // 群邀请信息变化的广播注册
        mLBM.registerReceiver(InviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    /**
     * 刷新页面
     */
    private void refresh() {
        // 获取所有邀请信息
        List<InvitationInfo> invitations = Model.getInstance().getDbManager().getInvitationDao().getInvitations();
        inviteAdapter.refresh(invitations);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解注册
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }
}
