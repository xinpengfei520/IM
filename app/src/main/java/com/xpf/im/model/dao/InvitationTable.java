package com.xpf.im.model.dao;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:创建邀请信息的表
 */

public class InvitationTable {

    public static final String TAB_NAME = "tab_invitation";

    public static final String COL_USER_HXID = "user_hxid";         // 用户的环信id
    public static final String COL_USER_NAME = "user_name";         // 用户的名称

    public static final String COL_GROUP_HXID = "group_hxid";       // 群id
    public static final String COL_GROUP_NAME = "group_name";       // 群名称

    public static final String COL_REASON = "reason";               // 原因
    public static final String COL_STATUS = "status";               // 状态


    public static final String CREATE_TABLE = "create table "
            + InvitationTable.TAB_NAME + " ("
            + InvitationTable.COL_USER_HXID + " text primary key, "
            + InvitationTable.COL_USER_NAME + " text, "
            + InvitationTable.COL_GROUP_HXID + " text, "
            + InvitationTable.COL_GROUP_NAME + " text, "
            + InvitationTable.COL_REASON + " text, "
            + InvitationTable.COL_STATUS + " integer); ";
}
