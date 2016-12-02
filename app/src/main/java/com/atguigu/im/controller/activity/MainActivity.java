package com.atguigu.im.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.atguigu.im.R;
import com.atguigu.im.controller.fragment.ContactListFragment;
import com.atguigu.im.controller.fragment.ConversationFragment;
import com.atguigu.im.controller.fragment.DiscoverFragment;
import com.atguigu.im.controller.fragment.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.atguigu.im.R.id.rg_main;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;

    @BindView(R.id.rb_main_chat)
    RadioButton rbMainChat;

    @BindView(R.id.rb_main_contact)
    RadioButton rbMainContact;

    @BindView(R.id.rb_main_discover)
    RadioButton rbMainDiscover;

    @BindView(R.id.rb_main_setting)
    RadioButton rbMainSetting;

    @BindView(rg_main)
    RadioGroup rgMain;

    private ConversationFragment conversationFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;
    private DiscoverFragment discoverFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();

        // 默认选择会话列表页面
        rgMain.check(R.id.rb_main_chat);

        rgMain.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    private void initData() {
        conversationFragment = new ConversationFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
        discoverFragment = new DiscoverFragment();
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            Fragment fragment = null;

            switch (checkedId) {
                case R.id.rb_main_chat:// 会话列表页面
                    fragment = conversationFragment;
                    break;
                case R.id.rb_main_contact:// 联系人列表页面
                    fragment = contactListFragment;
                    break;
                case R.id.rb_main_discover:// 发现列表页面
                    fragment = discoverFragment;
                    break;
                case R.id.rb_main_setting:// 设置列表页面
                    fragment = settingFragment;
                    break;
            }

            switchFragment(fragment);
        }
    }

    /**
     * 切换到不同的Fragment
     *
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main, fragment)
                .commit();
    }

}
