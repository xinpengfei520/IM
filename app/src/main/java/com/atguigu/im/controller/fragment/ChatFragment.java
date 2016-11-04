package com.atguigu.im.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atguigu.im.R;

/**
 * Created by xpf on 2016/11/2 :)
 * Wechat:18091383534
 * Function:
 */

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.chatactivity,null);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
