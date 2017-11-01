package com.fpliu.newton.ui.image.crop;

import android.app.Activity;
import android.content.Intent;
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
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.Util;
import com.fpliu.newton.ui.image.loader.ImageLoaderManager;
import com.fpliu.newton.ui.image.view.AvatarRectView;
import com.fpliu.newton.ui.image.view.SuperImageView;

import java.io.File;


/**
 * 裁剪头像界面
 */
public class ImageCropActivity extends BaseActivity implements View.OnClickListener {

    private static final String KEY_IMAGE_PATH = "imagePath";

    private static final String KEY_CROP_WIDTH = "cropWidth";

    private static ImageCropCompleteListener imageCropCompleteListener;

    private SuperImageView superImageView;
    private AvatarRectView mRectView;

    private String imagePath;

    private int cropWidth;

    public static void startForResult(int requestCode, Activity activity, String imagePath, int cropWidth, ImageCropCompleteListener imageCropCompleteListener) {
        ImageCropActivity.imageCropCompleteListener = imageCropCompleteListener;
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, imagePath);
        intent.putExtra(KEY_CROP_WIDTH, cropWidth);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_IMAGE_PATH, imagePath);
        outState.putInt(KEY_CROP_WIDTH, cropWidth);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("裁剪");

        addViewInBody(R.layout.activity_crop);

        FrameLayout rootView = (FrameLayout) findViewById(R.id.container);
        mRectView = new AvatarRectView(me(), UIUtil.getScreenWidth(me()) - 30 * 2);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(mRectView, 1, lp);

        superImageView = (SuperImageView) findViewById(R.id.iv_pic);

        findViewById(R.id.btn_pic_ok).setOnClickListener(this);
        findViewById(R.id.btn_pic_rechoose).setOnClickListener(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            imagePath = intent.getStringExtra(KEY_IMAGE_PATH);
            cropWidth = intent.getIntExtra(KEY_CROP_WIDTH, 256);
        } else {
            imagePath = savedInstanceState.getString(KEY_IMAGE_PATH);
            cropWidth = savedInstanceState.getInt(KEY_CROP_WIDTH, 256);
        }
        ImageLoaderManager.getImageLoader().displayImage(superImageView, Uri.fromFile(new File(imagePath)).toString(), R.drawable.image_default);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pic_ok) {
            Bitmap bitmap = getCropBitmap(cropWidth);
            if (imageCropCompleteListener != null) {
                imageCropCompleteListener.onImageCropComplete(bitmap, imagePath);
                imageCropCompleteListener = null;
            }
            finish();
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
