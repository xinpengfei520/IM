package com.xpf.im.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xpf.im.model.bean.UserInfo;
import com.xpf.im.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function: 联系人表的操作类
 */

public class ContactDao {

    private DBHelper mHelper;

    // 接收数据库对象
    public ContactDao(DBHelper helper) {
        mHelper = helper;
    }

    // 获取所有联系人
    public List<UserInfo> getContacts() {

        // 1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行查询语句(1代表是联系人,0代表非联系人)
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.IS_CONTACT + "=1";
        Cursor cursor = db.rawQuery(sql, null);

        List<UserInfo> users = new ArrayList<>();

        while (cursor.moveToNext()) {
            //封装对象
            UserInfo userInfo = new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

            users.add(userInfo);
        }

        //3. 关闭资源
        cursor.close();

        //4.返回数据
        return users;
    }

    // 通过环信id获取联系人单个信息
    public UserInfo getContactByHx(String hxId) {

        //校验
        if (hxId == null) {
            return null;
        }

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        UserInfo userInfo = null;

        if (cursor.moveToNext()) {

            userInfo = new UserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }

        //3.关闭资源
        cursor.close();

        //4.返回数据
        return userInfo;
    }

    //通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds) {

        //校验
        if (hxIds == null || hxIds.size() == 0) {
            return null;
        }

        List<UserInfo> users = new ArrayList<>();

        for (String hxid : hxIds) {
            //查询单个人
            UserInfo userInfo = getContactByHx(hxid);
            users.add(userInfo);
        }

        return users;
    }

    //保存单个联系人
    public void saveContact(UserInfo user, boolean isMyContact) {

        if (user == null) {
            return;
        }

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行添加操作
        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_HXID, user.getHxid());
        values.put(ContactTable.COL_NAME, user.getName());
        values.put(ContactTable.COL_NICK, user.getNick());
        values.put(ContactTable.COL_PHOTO, user.getPhoto());
        values.put(ContactTable.IS_CONTACT, isMyContact ? 1 : 0);

        db.replace(ContactTable.TAB_NAME, null, values);
    }

    //保存一组联系人
    public void saveContact(List<UserInfo> contacts, boolean isMyContact) {

        //校验
        if (contacts == null || contacts.size() == 0) {
            return;
        }

        for (UserInfo contact : contacts) {
            //向数据库中添加一个用户
            saveContact(contact, isMyContact);
        }
    }

    //删除联系人信息
    public void deleteContactByHxId(String hxId) {

        //校验
        if (hxId == null) {
            return;
        }

        //1.获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行删除语句
        db.delete(ContactTable.TAB_NAME, ContactTable.COL_HXID + "=?", new String[]{hxId});
    }

}
