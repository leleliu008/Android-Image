package com.fpliu.newton.ui.image.crop;

import android.graphics.Bitmap;

public interface CropCompleteListener {
    void onImageCropComplete(Bitmap bmp, float ratio);
}
