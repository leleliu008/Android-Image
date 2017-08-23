package com.fpliu.newton.ui.image;

import android.widget.ImageView;

public interface ImageLoader {

    void onLoad(ImageView imageView, String imageUri, int defaultImgId);
}
