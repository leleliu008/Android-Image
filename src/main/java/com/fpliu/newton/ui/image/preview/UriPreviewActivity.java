package com.fpliu.newton.ui.image.preview;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.fpliu.newton.ui.base.UIUtil;

import java.util.ArrayList;

/**
 * @author 792793182@qq.com 2016-07-12.
 */
public final class UriPreviewActivity extends BasePreviewActivity<String> {

    public static void start(Activity activity, int initPosition, ArrayList<String> images) {
        if (images == null || images.isEmpty()) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, UriPreviewActivity.class);
        intent.putExtra(KEY_POSITION, initPosition);
        intent.putExtra(KEY_IMAGES, images);
        activity.startActivity(intent);
    }


    public static void start(Activity activity, String imageURI) {
        if (TextUtils.isEmpty(imageURI)) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> images = new ArrayList<>(1);
        images.add(imageURI);
        start(activity, 0, images);
    }

    @Override
    protected String getUri(int position, String item) {
        return item;
    }
}
