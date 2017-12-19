package com.xpf.im.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.xpf.im.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoDetailsActivity extends Activity {

    @BindView(R.id.iv_photo)
    ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);
        ButterKnife.bind(this);

        Bundle message = getIntent().getExtras();
        ivPhoto.setImageResource(message.describeContents());
    }
}
