package com.xpf.im.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xpf.im.model.bean.GroupInfo;
import com.xpf.im.model.bean.InvitationInfo;
import com.xpf.im.model.bean.UserInfo;
import com.xpf.im.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:邀请(群/好友)操作的类
 */

public class InvitationDao {

    private DBHelper mHelper;

    public InvitationDao(DBHelper dbHelper) {
        mHelper = dbHelper;
    }

    // 添加邀请
    public void addInvitation(InvitationInfo invitationInfo) {

        // 1获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // 2 执行添加操作
        ContentValues values = new ContentValues();

        //邀请原因
        values.put(InvitationTable.COL_REASON, invitationInfo.getReason());

        //邀请状态
        values.put(InvitationTable.COL_STATUS, invitationInfo.getStatus().ordinal());

        //区分是好友邀请还是群邀请
        UserInfo user = invitationInfo.getUser();

        if (user == null) {//群邀请
            values.put(InvitationTable.COL_GROUP_HXID, invitationInfo.getGroup().getGroupId());// 群id
            values.put(InvitationTable.COL_GROUP_NAME, invitationInfo.getGroup().getGroupName());//群名称
            values.put(InvitationTable.COL_USER_HXID, invitationInfo.getGroup().getInvatePerson());// 群邀请人

        } else {//好友邀请
            values.put(InvitationTable.COL_USER_HXID, invitationInfo.getUser().getHxid());// 环信id
            values.put(InvitationTable.COL_USER_NAME, invitationInfo.getUser().getName());// 名称
        }

        db.replace(InvitationTable.TAB_NAME, null, values);
    }

    // 获取所有邀请信息
    public List<InvitationInfo> getInvitations() {
        // 1 获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // 2 执行查询语句
        String sql = "select * from " + InvitationTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<InvitationInfo> invitationInfos = new ArrayList<>();

        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();

            //原因
            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_REASON)));
            //状态
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InvitationTable.COL_STATUS))));

            // 区分好友邀请还是群邀请
            String GroupId = cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_HXID));

            if (GroupId == null) {// 好友邀请
                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_NAME)));

                invitationInfo.setUser(userInfo);
            } else {// 群邀请
                GroupInfo groupInfo = new GroupInfo();

                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InvitationTable.COL_USER_HXID)));

                invitationInfo.setGroup(groupInfo);
            }

            invitationInfos.add(invitationInfo);
        }

        // 3 关闭资源
        cursor.close();

        // 4 返回数据
        return invitationInfos;
    }

    // 将int类型状态转换为邀请的状态
    private InvitationInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    //删除邀请
    public void removeInvitation(String hxId) {
        //校验
        if (hxId == null) {
            return;
        }

        //1.获取数据库的连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行删除语句
        db.delete(InvitationTable.TAB_NAME, InvitationTable.COL_USER_HXID + "=?", new String[]{hxId});
    }

    //更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus invitationStatus, String hxId) {

        if (hxId == null) {
            return;
        }

        //1.获取数据库的连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //2.执行更新语句
        ContentValues values = new ContentValues();

        values.put(InvitationTable.COL_STATUS, invitationStatus.ordinal());

        db.update(InvitationTable.TAB_NAME, values, InvitationTable.COL_USER_HXID + "=?", new String[]{hxId});
    }
}
