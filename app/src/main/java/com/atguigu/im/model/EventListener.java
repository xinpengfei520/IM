package com.atguigu.im.model;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by xpf on 2016/11/4 :)
 * Wechat:18091383534
 * Function:全局事件的监听类
 */

public class EventListener {

    private Context mContext;

    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;

        //创建一个发送广播管理者的对象
        mLBM = LocalBroadcastManager.getInstance(mContext);

        // 注册一个联系人变化的监听


        // 注册一个群组信息变化的监听
    }
}
