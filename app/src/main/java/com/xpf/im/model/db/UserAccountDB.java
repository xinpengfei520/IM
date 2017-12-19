package com.xpf.im.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xpf.im.model.dao.UserAccountTable;

/**
 * Created by xpf on 2016/11/1 :)
 * Wechat:18091383534
 * Function:用户账号的数据库
 */

public class UserAccountDB extends SQLiteOpenHelper {

    public UserAccountDB(Context context) {
        super(context, "account.db", null, 1);
    }

    /**
     * 创建时调用
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table tab_account ( hxid text primary key, name text, nick text, photo text);");
        db.execSQL(UserAccountTable.CREATE_TAB);
    }

    /**
     * 数据库更新时调用
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
