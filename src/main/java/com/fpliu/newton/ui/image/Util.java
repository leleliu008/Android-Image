package com.fpliu.newton.ui.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.fpliu.newton.log.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Util {

    private static final String TAG = Util.class.getSimpleName();

    public static boolean isStorageEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap.recycle();
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return bitmap;
    }

    public static Bitmap makeCropBitmap(Bitmap bitmap, Rect rectBox, RectF imageMatrixRect, int expectSize) {
        Bitmap bmp = bitmap;
        RectF localRectF = imageMatrixRect;
        float f = localRectF.width() / bmp.getWidth();
        int left = (int) ((rectBox.left - localRectF.left) / f);
        int top = (int) ((rectBox.top - localRectF.top) / f);
        int width = (int) (rectBox.width() / f);
        int height = (int) (rectBox.height() / f);

        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }

        if (left + width > bmp.getWidth()) {
            width = bmp.getWidth() - left;
        }
        if (top + height > bmp.getHeight()) {
            height = bmp.getHeight() - top;
        }

        int k = width;
        if (width < expectSize) {
            k = expectSize;
        }
        if (width > expectSize) {
            k = expectSize;
        }

        try {
            bmp = Bitmap.createBitmap(bmp, left, top, width, height);

            if (k != width && k != height) {//don't do this if equals
                bmp = Bitmap.createScaledBitmap(bmp, k, k, true);//scale the bitmap
            }

        } catch (OutOfMemoryError error) {
            Logger.e(TAG, "OOM when create bitmap", error);
        }
        return bmp;
    }

    /**
     * 在Activity中调用拍照
     */
    public static File takePicture(Activity activity, int requestCode) {
        File photoFile = null;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 确保有正确的程序可以处理
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = createImageFile();
            photoFile = createImageSaveFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        activity.startActivityForResult(intent, requestCode);
        return photoFile;
    }

    /**
     * 在Fragment中调用拍照
     */
    public void takePicture(Fragment fragment, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = createImageFile();
            File photoFile = createImageSaveFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    private static File createImageSaveFile() {
        if (Util.isStorageEnable()) {
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!pic.exists()) {
                pic.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            return new File(pic, "IMG_" + timeStamp + ".jpg");
        } else {
            File cacheDir = Environment.getDataDirectory();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            return new File(cacheDir, "IMG_" + timeStamp + ".jpg");
        }
    }

    /**
     * scan the photo so that the gallery can read it
     *
     * @param context
     * @param path
     */
    public static void galleryAddPic(Context context, String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.fromFile(new File(path))));
    }

    private Util() {
        throw new AssertionError("No Instances");
    }

}
