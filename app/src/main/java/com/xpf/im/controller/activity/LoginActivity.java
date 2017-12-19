package com.xpf.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.xpf.im.R;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        etUsername.setText("xin123");
        etPwd.setText("123456");
    }


    @OnClick({R.id.btn_regist, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_regist:
                regist();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        //1.获取输入的用户名和密码
        final String loginName = etUsername.getText().toString();
        final String loginPwd = etPwd.getText().toString();
        //2.校验用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.去环信服务器登录
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {

                    /**
                     * 当登录成功的时候的回调
                     */
                    @Override
                    public void onSuccess() {
                        // 模型层数据的初始化
                        Model.getInstance().loginSuccess(new UserInfo(loginName));
                        //保存登录信息
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));
                        //跳转到主页面
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        //销毁当前页面
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });

    }

    private void regist() {
        //1.获取输入的用户名和密码
        final String registName = etUsername.getText().toString();
        final String registPwd = etPwd.getText().toString();
        //2.校验用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.去环信服务器注册
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //注册
                try {
                    EMClient.getInstance().createAccount(registName, registPwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败!" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
