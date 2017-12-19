package com.xpf.im.model.dao;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:创建保存好友的表
 */

public class ContactTable {

    public static final String TAB_NAME = "tab_contact";
    public static final String COL_HXID = "name";
    public static final String COL_NAME = "hxid";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";

    public static final String IS_CONTACT = "is_contact";

    public static final String CREATE_TAB = "create table "
            + ContactTable.TAB_NAME + " ("
            + ContactTable.COL_HXID + " text primary key, "
            + ContactTable.COL_NAME + " text, "
            + ContactTable.COL_NICK + " text, "
            + ContactTable.COL_PHOTO + " text, "
            + ContactTable.IS_CONTACT + " integer); ";
}
