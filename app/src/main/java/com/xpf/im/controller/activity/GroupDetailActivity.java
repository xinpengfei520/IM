package com.xpf.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.xpf.im.ImApplication;
import com.xpf.im.R;
import com.xpf.im.controller.adapter.GroupDetailAdapter;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.UserInfo;
import com.xpf.im.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 群详情页面
 */
public class GroupDetailActivity extends Activity {

    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.btn_groupdetail_out)
    Button btnGroupdetailOut;

    private EMGroup mGroup;
    private List<UserInfo> mUsers;
    private LocalBroadcastManager mLBM;
    private GroupDetailAdapter groupDetailAdapter;

    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {

        // 添加群成员
        @Override
        public void onAddMembers() {
            //跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            //传递参数
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
            startActivityForResult(intent, 2);
        }

        // 删除群成员
        @Override
        public void onDeleteMember(final UserInfo user) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("TAG", "" + mGroup.getGroupId() + ":" + user.getHxid());
                        // 从群中删除相应的联系人
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxid());
                        // 从环信服务器获取群成员信息
                        getMemberFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除联系人成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除群成员失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // 获取到选择的所有联系人
            final String[] memberses = data.getStringArrayExtra("members");

            // 联网
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 向群中添加联系人
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), memberses);
                        // 内存和页面变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

        getData();      // 获取传递过来的数据
        initData();     // 初始化数据
        initListener(); // 初始化监听
    }

    private void initListener() {
        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 判断当前是否是删除模式
                        if (groupDetailAdapter.ismIsDeleteMode()) {
                            // 更改删除模式状态
                            groupDetailAdapter.setmIsDeleteMode(false);
                            // 刷新页面
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {

        initButtonDisplay();    // 1.初始化button显示
        initGridview();         // 2.初始化GridView
        getMemberFromHxServer();// 3.从环信服务器获取群成员信息
    }

    /**
     * 从环信服务器获取群成员信息
     */
    private void getMemberFromHxServer() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取该群信息
                    EMGroup groupsFromHx = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    // 获取群成员信息
                    List<String> members = groupsFromHx.getMembers();

                    if (members != null && members.size() >= 0) {
                        mUsers = new ArrayList<UserInfo>();
                        // 转化
                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新页面
                                groupDetailAdapter.refresh(mUsers);
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initGridview() {

        // 如果当前是群主或者是公开的群，该群可以修改(添加或删除联系人)
        boolean isModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        groupDetailAdapter = new GroupDetailAdapter(this, isModify, mOnGroupDetailListener);
        gridview.setAdapter(groupDetailAdapter);
    }

    /**
     * 初始化button显示
     */
    private void initButtonDisplay() {

        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {//群主
            btnGroupdetailOut.setText("解散群");

            //解散群的点击事件处理
            btnGroupdetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //联网
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());
                                // 发送解散群的广播
                                exitGroupBroadcast();
                                // 页面和内存的处理
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else { // 群成员
            btnGroupdetailOut.setText("退群");

            // 退群的监听事件处理
            btnGroupdetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 去环信服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());
                                // 发送退群广播
                                exitGroupBroadcast();
                                //页面和内存的变化
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 发送退群广播
     */
    private void exitGroupBroadcast() {

        mLBM = LocalBroadcastManager.getInstance(ImApplication.getContext());
        Intent intent = new Intent(Constant.EXIT_GROUP);
        // 传递参数
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    /**
     * 获取传递过来的数据
     */
    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId == null) {
            finish();
            return;
        } else {
            // 获取到群信息
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }
    }
}
