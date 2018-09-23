package com.xpf.im.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by xpf on 2016/11/6 :)
 * Wechat:18091383534
 * Function:发现页面的Fragment
 */

public class DiscoverFragment extends Fragment {

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        textView = new TextView(getActivity());
        textView.setText("发现页面");
        textView.setTextSize(20);
        return textView;
    }
}
