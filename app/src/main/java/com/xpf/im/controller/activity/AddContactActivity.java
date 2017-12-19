package com.xpf.im.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.xpf.im.R;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加联系人页面
 */
public class AddContactActivity extends Activity {

    @BindView(R.id.tv_search)
    TextView tvSearch;

    @BindView(R.id.et_userId)
    EditText etUserId;

    @BindView(R.id.tv_username)
    TextView tvUsername;

    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;

    @BindView(R.id.btn_add)
    Button btnAdd;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.tv_search, R.id.btn_add, R.id.rl_add})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_search:// 查询按钮的点击事件处理
                toSearch();
                break;

            case R.id.btn_add:// 添加按钮的点击事件处理
                addFriend();
                break;

            case R.id.rl_add:// 整个条目的点击事件处理
                break;
        }
    }

    /**
     * 添加好友
     */
    private void addFriend() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getHxid(), "添加好友邀请");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送好友邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    /**
     * 根据id搜索用户
     */
    private void toSearch() {
        // 1.获取输入的用户名名称
        final String addName = etUserId.getText().toString();

        //2.判空
        if (TextUtils.isEmpty(addName)) {
            Toast.makeText(AddContactActivity.this, "查找的用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.去环信服务器查找该用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去服务器获取该用户信息
                userInfo = new UserInfo(addName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示布局
                        rlAdd.setVisibility(View.VISIBLE);

                        //显示昵称
                        tvUsername.setText(userInfo.getName());
                    }
                });
            }
        });
    }


}
