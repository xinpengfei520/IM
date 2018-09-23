package com.xpf.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;
import com.xpf.im.R;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.UserInfo;

public class WelcomeActivity extends Activity {

    private UserInfo account;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //当前Activity已经销毁
            if (isFinishing()) {
                return;
            }
            //跳转到主页面或者登陆页面
            toMainOrLogin();
        }
    };

    private void toMainOrLogin() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //分两种情况
                if (EMClient.getInstance().isLoggedInBefore()) {//登录过
                    //获取登录过的用户信息
                    account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                    if (account == null) {
                        //跳转到登录页面
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    } else {
                        // 当登录成功的回调创建DBManager对象
                        Model.getInstance().loginSuccess(account);
                        //跳转到主页面
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    }
                } else {//没登录过
                    //跳转到登录页面
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
                //结束当前页面
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 发送一个延时2s的消息
        handler.sendMessageDelayed(Message.obtain(), 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
