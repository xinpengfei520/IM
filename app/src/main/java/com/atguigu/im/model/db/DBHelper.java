package com.atguigu.im.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atguigu.im.model.dao.ContactTable;
import com.atguigu.im.model.dao.InvitationTable;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:数据库帮助类(用于创建表)
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建联系人的表
        db.execSQL(ContactTable.CREATE_TAB);

        // 创建邀请信息的表
        db.execSQL(InvitationTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
