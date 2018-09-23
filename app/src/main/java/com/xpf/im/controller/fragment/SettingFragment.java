package com.xpf.im.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.xpf.im.R;
import com.xpf.im.controller.activity.LoginActivity;
import com.xpf.im.controller.activity.MyCodeActivity;
import com.xpf.im.model.Model;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:设置页面的Fragment
 */

public class SettingFragment extends Fragment {

    @BindView(R.id.btn_logout)
    Button btnLogout;

    @BindView(R.id.tv_my_code)
    TextView tvMyCode;

    private String currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.setting_fragment, null);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    //初始化页面数据
    private void initData() {
        currentUser = EMClient.getInstance().getCurrentUser();
        btnLogout.setText("退出登录(" + currentUser + ")");
    }

    @OnClick({R.id.tv_my_code, R.id.btn_logout})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_logout:

                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        // 去环信服务器请求退出
                        EMClient.getInstance().logout(false, new EMCallBack() {

                            // 退出成功的时候的回调
                            @Override
                            public void onSuccess() {

                                // 跳转到登录页面
                                startActivity(new Intent(getActivity(), LoginActivity.class));

                                // 结束当前页面
                                getActivity().finish();
                            }

                            @Override
                            public void onError(int i, String s) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "退出登录失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
                    }
                });
                break;

            case R.id.tv_my_code:
                Log.e("TAG", "userName==" + currentUser);
                Intent intent = new Intent(getActivity(), MyCodeActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                break;
        }
    }

}
