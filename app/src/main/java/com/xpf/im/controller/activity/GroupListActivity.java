package com.atguigu.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.atguigu.im.R;
import com.atguigu.im.controller.adapter.GroupListAdapter;
import com.atguigu.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 群组列表页面
 */
public class GroupListActivity extends Activity {

    @BindView(R.id.lv_grouplist)
    ListView lvGrouplist;

    LinearLayout llGroupList;

    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);

        initData();

        initListener();
    }

    private void initData() {

        // 添加头布局
        View headerview = View.inflate(this, R.layout.header_group_list, null);
        llGroupList = (LinearLayout) headerview.findViewById(R.id.ll_group_list);
        lvGrouplist.addHeaderView(headerview);

        //  初始化listView
        groupListAdapter = new GroupListAdapter(this);

        lvGrouplist.setAdapter(groupListAdapter);

        // 从环信服务器获取群组信息
        getGroupFromHxServer();
    }

    private void getGroupFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 去网络获取群信息
                    List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    if (mGroups != null && mGroups.size() > 0) {
                        //内存和页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新方法
                                refresh();
                                // 页面处理
                                Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void refresh() {

        // 获取群组相关数据
        List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();

        //调用刷新方法
        groupListAdapter.refresh(allGroups);
    }

    private void initListener() {
        llGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到新建群页面
                startActivity(new Intent(GroupListActivity.this, NewGroupActivity.class));
            }
        });

        // listview条目的点击事件
        lvGrouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return;
                }
                position -= 1;

                Log.e("TAG", "position" + position);

                // 跳转到会话详情页面
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);

                //传递数据
                List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();

                // 设置环信id
                intent.putExtra(EaseConstant.EXTRA_USER_ID, allGroups.get(position).getGroupId());

                // 设置会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //刷新页面
        refresh();
    }

}