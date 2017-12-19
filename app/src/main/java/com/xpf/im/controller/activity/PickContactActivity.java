package com.xpf.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.xpf.im.R;
import com.xpf.im.controller.adapter.PickContactAdapter;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.PickContactInfo;
import com.xpf.im.model.bean.UserInfo;
import com.xpf.im.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 选择联系人页面
 */
public class PickContactActivity extends Activity {

    @BindView(R.id.lv_pickcontact)
    ListView lvPickcontact;
    @BindView(R.id.tv_pickcontact_save)
    TextView tvPickcontactSave;

    private List<String> mExistMembers;
    private List<PickContactInfo> mPickContacts;
    private PickContactAdapter pickContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.bind(this);

        getData();      // 获取传递过来的数据
        initData();     // 初始化数据
        initListener(); // 初始化监听
    }

    private void getData() {

        // 获取传递过来的群Id
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId != null) { // 说明是回传过来的数据
            // 获取该群的信息
            EMGroup emGroup = EMClient.getInstance().groupManager().getGroup(groupId);
            // 获取该群的群成员信息
            mExistMembers = emGroup.getMembers();
        } else {
            mExistMembers = new ArrayList<>();
        }
    }

    private void initListener() {

        // listview条目的点击事件
        lvPickcontact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获取checkbox对象
                CheckBox cb_pickcontact = (CheckBox) view.findViewById(R.id.cb_pickcontact);
                cb_pickcontact.setChecked(!cb_pickcontact.isChecked());
                // 内存
                PickContactInfo pickContactInfo = mPickContacts.get(position);
                pickContactInfo.setChecked(cb_pickcontact.isChecked());
                // 刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });

        // 保存按钮的点击事件
        tvPickcontactSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 获取已经选中的联系人
                List<String> picks = pickContactAdapter.getPickContacts();
                // 给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", picks.toArray(new String[0]));
                // 设置返回的结果码
                setResult(RESULT_OK, intent);

                finish(); // 结束当前页面
            }
        });
    }

    private void initData() {

        // 初始化item数据(从本地获取)
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactDao().getContacts();

        if (contacts != null && contacts.size() >= 0) {
            mPickContacts = new ArrayList<>();
            // 转化
            for (UserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPickContacts.add(pickContactInfo);
            }
        }

        // 初始化Listview适配器
        pickContactAdapter = new PickContactAdapter(PickContactActivity.this, mPickContacts, mExistMembers);
        lvPickcontact.setAdapter(pickContactAdapter);
    }
}
