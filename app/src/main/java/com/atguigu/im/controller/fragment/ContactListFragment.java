package com.atguigu.im.controller.fragment;

import android.content.Intent;
import android.view.View;

import com.atguigu.im.R;
import com.atguigu.im.controller.activity.AddContactActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:
 */

public class ContactListFragment extends EaseContactListFragment {

    @Override
    protected void initView() {
        super.initView();

        //添加加号
        titleBar.setRightImageResource(R.drawable.em_add);

        // 添加头布局
        View headerview = View.inflate(getActivity(), R.layout.header_contactlist, null);

        listView.addHeaderView(headerview);
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到添加联系人页面
                startActivity(new Intent(getActivity(),AddContactActivity.class));
            }
        });
    }
}
