package com.atguigu.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.atguigu.im.R;
import com.atguigu.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新建群组页面
 */
public class NewGroupActivity extends Activity {

    @BindView(R.id.et_newgroup_name)
    EditText etNewgroupName;

    @BindView(R.id.et_newgroup_desc)
    EditText etNewgroupDesc;

    @BindView(R.id.cb_newgroup_public)
    CheckBox cbNewgroupPublic;

    @BindView(R.id.cb_newgroup_invite)
    CheckBox cbNewgroupInvite;

    @BindView(R.id.btn_newgroup_create)
    Button btnNewgroupCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {

    }

    @OnClick(R.id.btn_newgroup_create)
    void btn_new_create(View view) {

        // 跳转到选择联系人页面
        startActivityForResult(new Intent(NewGroupActivity.this, PickContactActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //如果获取到选择的联系人，我就要创建群，否则就不创建
        if (resultCode == RESULT_OK) {
            // 创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    /**
     * 创建群组
     *
     * @param members
     */
    private void createGroup(final String[] members) {
        // 获取群简介
        final String groupName = etNewgroupName.getText().toString();
        //获取群简介
        final String groupDesc = etNewgroupDesc.getText().toString();

        // 校验
        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(NewGroupActivity.this, "输入的群名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //联网
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                    options.maxUsers = 200;
                    EMGroupManager.EMGroupStyle groupStyle = null;

                    //是否公开
                    if (cbNewgroupPublic.isChecked()) {

                        // 是否开放群邀请
                        if (cbNewgroupInvite.isChecked()) {
                            groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                        } else {
                            groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                        }

                    } else {

                        //是否开放群邀请
                        if (cbNewgroupInvite.isChecked()) {
                            groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                        } else {
                            groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                        }
                    }
                    options.style = groupStyle;

                    //参数一:群名称，参数二：群简介，参数三：群成员，参数四：创建群的原因，参数五：参数配置
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "创建群", options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();
                            // 结束当前页面
                            finish();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
