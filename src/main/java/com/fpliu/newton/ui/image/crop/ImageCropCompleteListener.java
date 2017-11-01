package com.fpliu.newton.ui.image.crop;

import android.graphics.Bitmap;

public interface ImageCropCompleteListener {
    void onImageCropComplete(Bitmap croppedBitmap, String sourceFilePath);
}
