package com.xpf.im.controller.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.xpf.im.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCodeActivity extends Activity {

    @BindView(R.id.iv_my_code)
    ImageView ivMyCode;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_code);
        ButterKnife.bind(this);

        user = getIntent().getExtras().getString("user");

        produce(user);
    }

    /**
     * 生成的回调
     *
     * @param user
     */
    public void produce(String user) {

        if ("".equals(user)) {
            Toast.makeText(MyCodeActivity.this, "生成二维码失败", Toast.LENGTH_SHORT).show();
        } else {

            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person);
            Bitmap bitmap = EncodingUtils.createQRCode(user.trim(), 500, 500, logoBitmap);
            ivMyCode.setImageBitmap(bitmap);
        }

    }

}
