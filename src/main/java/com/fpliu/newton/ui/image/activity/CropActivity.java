package com.fpliu.newton.ui.image.activity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.fpliu.newton.ui.base.BaseActivity;
import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.ImageManager;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.Util;
import com.fpliu.newton.ui.image.view.AvatarRectView;
import com.fpliu.newton.ui.image.view.SuperImageView;

import java.io.File;


/**
 * 裁剪头像界面
 */
public class CropActivity extends BaseActivity implements View.OnClickListener {

    private SuperImageView superImageView;
    private AvatarRectView mRectView;

    private String imagePath;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ImageManager.KEY_PIC_PATH, imagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentView(R.layout.activity_crop);

        setTitle("裁剪");

        FrameLayout rootView = (FrameLayout) findViewById(R.id.container);
        mRectView = new AvatarRectView(me(), UIUtil.getScreenWidth(me()) - 30 * 2);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(mRectView, 1, lp);

        superImageView = (SuperImageView) findViewById(R.id.iv_pic);

        findViewById(R.id.btn_pic_ok).setOnClickListener(this);
        findViewById(R.id.btn_pic_rechoose).setOnClickListener(this);

        if (savedInstanceState == null) {
            imagePath = getIntent().getStringExtra(ImageManager.KEY_PIC_PATH);
        } else {
            imagePath = savedInstanceState.getString(ImageManager.KEY_PIC_PATH);
        }
        ImageManager.getImageLoader().onLoad(superImageView, Uri.fromFile(new File(imagePath)).toString(), R.drawable.default_img);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pic_ok) {
            Bitmap bmp = getCropBitmap(ImageManager.getInstance().cropWidth());
            finish();
            ImageManager.getInstance().notifyImageCropComplete(bmp, 0);
        } else if (v.getId() == R.id.btn_pic_rechoose) {
            finish();
        }
    }

    private Bitmap getCropBitmap(int expectSize) {
        if (expectSize <= 0) {
            return null;
        }
        Bitmap srcBitmap = ((BitmapDrawable) superImageView.getDrawable()).getBitmap();
        double rotation = superImageView.getImageRotation();
        int level = (int) Math.floor((rotation + Math.PI / 4) / (Math.PI / 2));
        if (level != 0) {
            srcBitmap = Util.rotate(srcBitmap, 90 * level);
        }
        Rect centerRect = mRectView.getCropRect();
        RectF matrixRect = superImageView.getMatrixRect();

        return Util.makeCropBitmap(srcBitmap, centerRect, matrixRect, expectSize);
    }
}
