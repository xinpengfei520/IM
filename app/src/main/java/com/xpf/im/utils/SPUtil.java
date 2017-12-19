package com.atguigu.im.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.atguigu.im.ImApplication;

/**
 * Created by xpf on 2016/11/4 :)
 * Wechat:18091383534
 * Function: sp存储的工具类
 */

public class SPUtil {

    public static final String IS_NEW_INVITE = "is_new_invite";

    private SPUtil() {
    }

    private static SPUtil instace = new SPUtil();
    private static SharedPreferences mSp = null;

    public static SPUtil getInstace() {

        if (mSp == null) {
            mSp = ImApplication.getContext().getSharedPreferences("im0714", Context.MODE_PRIVATE);
        }

        return instace;
    }

    // 保存
    public void save(String key, Object value) {

        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 读取
    // 读取String类型数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 读取boolean类型数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 读取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

}
