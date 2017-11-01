package com.fpliu.newton.ui.image;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.fpliu.newton.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageCompressHelper {

    private static final String TAG = ImageCompressHelper.class.getSimpleName();

    /**
     * 最大宽度，默认为720
     */
    private float maxWidth = 720.0f;

    /**
     * 最大高度,默认为960
     */
    private float maxHeight = 960.0f;


    /**
     * 最大文件大小（单位：byte），默认100KB
     */
    private float maxFileSize = 100 * 1024;

    /**
     * 默认压缩后的方式为JPEG
     */
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    /**
     * 默认的图片处理方式是ARGB_8888
     */
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;

    /**
     * 默认压缩质量为80
     */
    private int quality = 80;

    /**
     * 存储路径，默认是/sdcard/{packageName}/image/
     */
    private String destinationDir;

    /**
     * 文件名，默认是{@link System#currentTimeMillis()}.jpg
     */
    private String fileNamePrefix;

    /**
     * 是否要隐藏（不让图库等软件读取到，实现方法是把后缀名设置为.xml）
     */
    private boolean hide = true;

    /**
     * 设置图片最大宽度
     *
     * @param maxWidth 最大宽度
     */
    public ImageCompressHelper maxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        return this;

    }

    /**
     * 设置图片最大高度
     *
     * @param maxHeight 最大高度
     */
    public ImageCompressHelper maxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    /**
     * 设置图片文件的最大文件大小（单位：byte）
     *
     * @param maxFileSize 最大文件大小
     */
    public ImageCompressHelper maxFileSize(float maxFileSize) {
        this.maxFileSize = maxFileSize;
        return this;
    }

    /**
     * 设置压缩的后缀格式
     */
    public ImageCompressHelper format(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    /**
     * 设置Bitmap的参数
     */
    public ImageCompressHelper config(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
        return this;
    }

    /**
     * 设置压缩质量，建议80
     *
     * @param quality 压缩质量，[0,100]
     */
    public ImageCompressHelper quality(int quality) {
        this.quality = quality;
        return this;
    }

    /**
     * 设置目的存储路径
     *
     * @param destinationDir 目的路径
     */
    public ImageCompressHelper destinationDir(String destinationDir) {
        this.destinationDir = destinationDir;
        return this;
    }

    /**
     * 设置文件名称
     *
     * @param fileNamePrefix 文件名前缀
     */
    public ImageCompressHelper fileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
        return this;
    }

    /**
     * 不要隐藏文件。默认是隐藏文件
     */
    public void dontHide() {
        this.hide = false;
    }

    /**
     * 压缩成文件
     *
     * @param sourceFile 原始文件
     * @return 压缩后的文件
     */
    public File toFile(Context context, File sourceFile) {
        long length = sourceFile.length();
        Logger.i(TAG, "sourceFileSize = " + (length / 1024) + "KB");
        Logger.i(TAG, "maxFileSize = " + (maxFileSize / 1024) + "KB");

        //如果文件本来就没有超过最大文件大小，就不用压缩了
        if (length <= maxFileSize) {
            return sourceFile;
        }
        if (TextUtils.isEmpty(destinationDir)) {
            destinationDir = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/image";
        }
        if (TextUtils.isEmpty(fileNamePrefix)) {
            fileNamePrefix = String.valueOf(System.currentTimeMillis());
        }

        File resultFile = compressImage(context.getApplicationContext(), Uri.fromFile(sourceFile), maxWidth, maxHeight,
                compressFormat, bitmapConfig, quality, destinationDir, "", fileNamePrefix, hide);
        Logger.i(TAG, "compressedFileSize = " + (resultFile.length() / 1024) + "KB");
        return resultFile;
    }

    /**
     * 压缩为Bitmap
     *
     * @param file 原始文件
     * @return 压缩后的Bitmap
     */
    public Bitmap toBitmap(Context context, File file) {
        if (TextUtils.isEmpty(destinationDir)) {
            destinationDir = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/image";
        }
        return getScaledBitmap(context.getApplicationContext(), Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }

    private static Bitmap getScaledBitmap(Context context, Uri imageUri, float maxWidth, float maxHeight, Bitmap.Config bitmapConfig) {
        String filePath = getRealPathFromURI(context, imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        if (actualHeight == -1 || actualWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(filePath);
                actualHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                actualWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (actualWidth <= 0 || actualHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
            if (bitmap2 != null) {
                actualWidth = bitmap2.getWidth();
                actualHeight = bitmap2.getHeight();
            } else {
                return null;
            }
        }

        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            // load the bitmap getTempFile its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    bmp = BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (bmp == null) {
            return null;
        }
        if (actualHeight <= 0 || actualWidth <= 0) {
            return null;
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, bitmapConfig);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, 0, 0);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    private File compressImage(Context context, Uri imageUri, float maxWidth, float maxHeight,
                              Bitmap.CompressFormat compressFormat, Bitmap.Config bitmapConfig,
                              int quality, String parentPath, String prefix, String fileName, boolean hide) {
        FileOutputStream out = null;
        String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase(), prefix, fileName, hide);
        try {
            out = new FileOutputStream(filename);
            //write the compressed bitmap at the destination specified by filename.
            getScaledBitmap(context, imageUri, maxWidth, maxHeight, bitmapConfig).compress(compressFormat, quality, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }

        return new File(filename);
    }

    private static String generateFilePath(Context context, String parentPath, Uri uri,
                                           String extension, String prefix, String fileName, boolean hide) {
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        /** if prefix is null, set prefix "" */
        prefix = TextUtils.isEmpty(prefix) ? "" : prefix;
        /** reset fileName by prefix and custom file name */
        fileName = TextUtils.isEmpty(fileName) ? prefix + splitFileName(getFileName(context, uri))[0] : fileName;
        String filePath = file.getAbsolutePath() + File.separator + fileName + "." + extension;
        return hide ? filePath + ".xml" : fileName;
    }


    /**
     * 计算inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * 获取真实的路径
     * @param context   上下文
     * @param uri       uri
     * @return          文件路径
     */
    private static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    /**
     * 截取文件名称
     * @param fileName  文件名称
     */
    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * 获取文件名称
     * @param context   上下文
     * @param uri       uri
     * @return          文件名称
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
