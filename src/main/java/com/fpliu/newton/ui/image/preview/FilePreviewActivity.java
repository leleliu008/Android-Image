package com.fpliu.newton.ui.image.preview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.fpliu.newton.ui.base.UIUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * @author 792793182@qq.com 2016-07-12.
 */
public final class FilePreviewActivity extends BasePreviewActivity<File> {

    public static void start(Activity activity, int initPosition, ArrayList<File> images) {
        if (images == null || images.isEmpty()) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, FilePreviewActivity.class);
        intent.putExtra(KEY_POSITION, initPosition);
        intent.putExtra(KEY_IMAGES, images);
        activity.startActivity(intent);
    }

    @Override
    protected String getUri(int position, File item) {
        return Uri.fromFile(item).toString();
    }
}
