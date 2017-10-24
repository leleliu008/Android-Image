package com.fpliu.newton.ui.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.bean.ImageItem;

import java.util.ArrayList;

/**
 * @author 792793182@qq.com 2016-07-12.
 */
public final class ImageItemPreviewActivity extends BasePreviewActivity<ImageItem> {

    public static void start(Activity activity, int initPosition, ArrayList<ImageItem> images) {
        if (images == null || images.isEmpty()) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, ImageItemPreviewActivity.class);
        intent.putExtra(KEY_POSITION, initPosition);
        intent.putExtra(KEY_IMAGES, images);
        activity.startActivity(intent);
    }


    public static void start(Activity activity, ImageItem imageItem) {
        if (imageItem == null) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<ImageItem> images = new ArrayList<>(1);
        images.add(imageItem);
        start(activity, 0, images);
    }

    @Override
    protected String getUri(int position, ImageItem item) {
        return item.path;
    }
}
