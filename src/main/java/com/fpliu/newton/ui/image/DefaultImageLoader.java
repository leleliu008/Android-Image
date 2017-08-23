package com.fpliu.newton.ui.image;

import android.net.Uri;
import android.widget.ImageView;

import com.fpliu.newton.log.Logger;

/**
 * 默认的图片加载器
 *
 */
public class DefaultImageLoader implements ImageLoader {

    private static final String TAG = DefaultImageLoader.class.getSimpleName();

    @Override
    public void onLoad(ImageView imageView, String imageUri, int defaultImgId) {
        try {
            imageView.setImageURI(Uri.parse(imageUri));
        } catch (Exception e) {
            Logger.e(TAG, "onLoad()", e);
            imageView.setImageResource(defaultImgId);
        }
    }
}
