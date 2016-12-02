package com.atguigu.im;

import android.app.Application;
import android.content.Context;

import com.atguigu.im.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by xpf on 2016/11/1 :)
 * Wechat:18091383534
 * Function:代表当前整个应用,用于初始化相关信息
 */

public class ImApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        // 初始化环信EaseUI
        initEaseUI();

        // 初始化模型层类
        Model.getInstance().init(this);
    }

    private void initEaseUI() {

        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);// 不总是接受群邀请
        options.setAutoAcceptGroupInvitation(false);// 不自动接受群邀请信息

        EaseUI.getInstance().init(this, options);
    }

    // 获取全局上下文
    public static Context getContext() {
        return mContext;
    }
}
