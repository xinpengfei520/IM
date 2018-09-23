package com.xpf.im.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xpf.im.model.bean.UserInfo;
import com.xpf.im.model.db.UserAccountDB;

/**
 * Created by xpf on 2016/11/1 :)
 * Wechat:18091383534
 * Function:用户操作数据库操作类
 */

public class UserAccountDao {

    private final UserAccountDB mHelper;

    public UserAccountDao(Context context) {

        // 创建数据库
        mHelper = new UserAccountDB(context);
    }

    // 添加用户到数据库中
    public void addAccount(UserInfo user) {

        //1.获取数据库的连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行添加语句
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_HXID, user.getHxid());
        values.put(UserAccountTable.COL_NAME, user.getName());
        values.put(UserAccountTable.COL_NICK, user.getNick());
        values.put(UserAccountTable.COL_PHOTO, user.getPhoto());

        db.replace(UserAccountTable.TAB_NAME, null, values);
    }


    // 根据环信id获取所有用户信息
    public UserInfo getAccountByHxId(String hxid) {


        //1.获取数据库的连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行查询语句
        String sql = "select * from " + UserAccountTable.TAB_NAME + " where " + UserAccountTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});

        UserInfo userInfo = null;

        if (cursor.moveToNext()) {

            //封装数据
            userInfo = new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }

        //3.关闭资源
        cursor.close();

        //4.返回数据
        return userInfo;
    }
}
