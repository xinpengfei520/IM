package com.xpf.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.xpf.im.R;
import com.xpf.im.model.Model;
import com.xpf.im.model.bean.UserInfo;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanResultActivity extends Activity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.rl_contact)
    RelativeLayout rlContact;

    private int requestCode;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        ButterKnife.bind(this);

        requestCode = 1;
        startActivityForResult(new Intent(ScanResultActivity.this, CaptureActivity.class), requestCode);
    }

    /**
     * 处理返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
//            tvName.setText(scanResult);
            checkIsExistOnHxServer(scanResult);
        }
    }

    private void checkIsExistOnHxServer(final String hxid) {

        //2.判空
        if (TextUtils.isEmpty(hxid)) {
            Toast.makeText(ScanResultActivity.this, "查找的用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.去环信服务器查找该用户是否存在
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去服务器获取该用户信息
                userInfo = new UserInfo(hxid);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示布局
                        rlContact.setVisibility(View.VISIBLE);

                        //显示昵称
                        tvName.setText(userInfo.getName());
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_add_friend)
    public void onClick() {

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getHxid(), "添加好友邀请");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScanResultActivity.this, "发送好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ScanResultActivity.this, "发送好友邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
