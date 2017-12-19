package com.xpf.im.model.dao;

/**
 * Created by xpf on 2016/11/1 :)
 * Wechat:18091383534
 * Function:保存用于账户信息的字段
 */

public class UserAccountTable {

    public static final String TAB_NAME = "tab_account";
    public static final String COL_HXID = "hxid";
    public static final String COL_NAME = "name";
    public static final String COL_NICK = "nick";
    public static final String COL_PHOTO = "photo";

    /**
     * 此处注意在拼接的时候的空格和逗号
     */
    public static final String CREATE_TAB = "create table "
            + UserAccountTable.TAB_NAME + " ("
            + UserAccountTable.COL_HXID + " text primary key, "
            + UserAccountTable.COL_NAME + " text, "
            + UserAccountTable.COL_NICK + " text, "
            + UserAccountTable.COL_PHOTO + " text)";
}
