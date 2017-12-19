package com.atguigu.im.model;

import android.content.Context;

import com.atguigu.im.model.bean.UserInfo;
import com.atguigu.im.model.dao.UserAccountDao;
import com.atguigu.im.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xpf on 2016/11/1 :)
 * Wechat:18091383534
 * Function:模型层全局类
 */

public class Model {

    private Context mContext;

    private static Model model = new Model();

    //创建全局线程池
    private ExecutorService executor = Executors.newCachedThreadPool();
    private UserAccountDao mUserAccountDao;
    private DBManager dbManager;

    public Model() {

    }

    // 单例
    public static Model getInstance() {
        return model;
    }

    // 初始化
    public void init(Context context) {
        mContext = context;

        //初始化用户账号数据库操作类
        mUserAccountDao = new UserAccountDao(context);

        // 初始化监听
        EventListener eventListener = new EventListener(mContext);
    }

    // 获取全局线程池
    public ExecutorService getGlobalThreadPool() {
        return executor;
    }

    // 获取用户账号数据库操作类
    public UserAccountDao getUserAccountDao() {
        return mUserAccountDao;
    }

    // 登陆成功后处理的事情
    public void loginSuccess(UserInfo userInfo) {

        // 校验
        if (userInfo == null) {
            return;
        }

        if (dbManager != null) {
            dbManager.close();
        }

        dbManager = new DBManager(mContext, userInfo.getName());
    }

    //获取数据库的管理者对象
    public DBManager getDbManager() {
        return dbManager;
    }
}
