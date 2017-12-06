package com.fpliu.newton.ui.image;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.fpliu.newton.log.Logger;

import java.io.File;
import java.io.FileInputStream;
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
     * 压缩成文件
     *
     * @param sourceFile 原始文件
     * @return 压缩后的文件
     */
    public File toFile(Context context, File sourceFile) {
        long length = sourceFile.length();
        log("sourceFile", sourceFile, null);
        log("maxFileSize", sourceFile, null);

        //如果文件本来就没有超过最大文件大小，就不用压缩了
        if (length <= maxFileSize) {
            log("destFile", sourceFile, null);
            return sourceFile;
        }
        //如果没有设置保存的文件夹，使用默认的目录
        if (TextUtils.isEmpty(destinationDir)) {
            destinationDir = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/image";
        }
        //如果没有设置保存的文件的文件名前缀，设置默认为时间戳
        if (TextUtils.isEmpty(fileNamePrefix)) {
            fileNamePrefix = String.valueOf(System.currentTimeMillis());
        }

        Uri sourceUri = Uri.fromFile(sourceFile);
        String destFileName = generateFilePath(context.getApplicationContext(), sourceUri, destinationDir, fileNamePrefix, splitFileName(getFileName(context, sourceUri))[0], compressFormat.name().toLowerCase());
        File destFile = new File(destFileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destFile);
            //缩放图片
            Bitmap bitmap = getScaledBitmap(context, sourceUri, maxWidth, maxHeight, bitmapConfig);
            if (bitmap == null) {
                log("destFile", sourceFile, null);
                return sourceFile;
            }
            //压缩成功
            if (bitmap.compress(compressFormat, quality, out)) {
                log("destFile", destFile, bitmap);
                return destFile;
            } else {
                log("destFile", sourceFile, null);
                return sourceFile;
            }
        } catch (Exception e) {
            Logger.e(TAG, "", e);
            log("destFile", sourceFile, null);
            return sourceFile;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, "", e);
            }
        }
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
        Logger.i(TAG, "getScaledBitmap() imageUri = " + imageUri + ", maxWidth = " + maxWidth + ", maxHeight = " + maxHeight + ", bitmapConfig = " + bitmapConfig);

        String filePath = getRealPathFromURI(context, imageUri);

        int actualHeight = 0;
        int actualWidth = 0;
        int orientation = 0;

        //尝试从EXIF中读取尺寸
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            //获取图片的高度
            actualHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
            //获取图片的宽度
            actualWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Logger.i(TAG, "ExifInterface: actualWidth = " + actualWidth + ", actualHeight = " + actualHeight + ", orientation = " + orientation);
        } catch (IOException e) {
            Logger.e(TAG, "", e);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = null;

        if (actualHeight <= 0 || actualWidth <= 0) {
            //只解析尺寸数据，不加载真正的图像数据
            options.inJustDecodeBounds = true;

            bmp = BitmapFactory.decodeFile(filePath, options);

            actualHeight = options.outHeight;
            actualWidth = options.outWidth;
            Logger.i(TAG, "actualWidth = options.outWidth = " + actualWidth + ", actualHeight = options.outHeight = " + actualHeight);

            if (actualHeight <= 0 || actualWidth <= 0) {
                if (bmp != null) {
                    actualWidth = bmp.getWidth();
                    actualHeight = bmp.getHeight();
                    Logger.i(TAG, "actualWidth = bmp.getWidth() = " + actualWidth + ", actualHeight = bmp.getHeight() = " + actualHeight);
                }
            }
        }

        //加载真正的图像数据
        options.inJustDecodeBounds = false;
        //缩放比率
        options.inSampleSize = calculateInSampleSize(actualWidth, actualHeight, maxWidth, maxHeight);

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    bmp = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (IOException e) {
                    Logger.e(TAG, "", e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            Logger.e(TAG, "", e);
                        }
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            Logger.e(TAG, "", e);
        }

        if (bmp == null) {
            return null;
        }
        if (orientation == 0) {
            Logger.i(TAG, "orientation == 0, bmp.getWidth() = " + bmp.getWidth() + ", bmp.getHeight() = " + bmp.getHeight());
            return bmp;
        }
        //如果图像角度不对，就对图像进行旋转
        else {
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            //对原图进行矩阵变换
            Bitmap newBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            if (newBitmap != bmp) {
                bmp.recycle();
                bmp = newBitmap;
            }
            Logger.i(TAG, "orientation == " + orientation + ", bmp.getWidth() = " + bmp.getWidth() + ", bmp.getHeight()" + bmp.getHeight());
            return bmp;
        }
    }

    private static String generateFilePath(Context context, Uri sourceUri, String destinationDir,
                                           String fileNamePrefix, String fileName, String extension) {
        File destDir = new File(destinationDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        if (fileNamePrefix == null) {
            fileNamePrefix = "";
        }

        if (fileName == null) {
            fileName = "";
        }

        return destDir.getAbsolutePath() + File.separator + fileNamePrefix + fileName + "." + extension;
    }

    /**
     * 计算inSampleSize
     */
    private static int calculateInSampleSize(int actualWidth, int actualHeight, float maxWidth, float maxHeight) {
        int inSampleSize = 1;

        if (actualWidth > maxWidth || actualHeight > maxHeight) {
            int heightRatio = Math.round(actualHeight / maxHeight);
            int widthRatio = Math.round(actualWidth / maxWidth);
            Logger.i(TAG, "heightRatio = " + heightRatio + ", widthRatio = " + widthRatio);
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
            if (inSampleSize == 3) {
                inSampleSize = 4;
            } else if (inSampleSize > 4 && inSampleSize < 8) {
                inSampleSize = 8;
            }
        }

        Logger.i(TAG, "inSampleSize = " + inSampleSize);

        return inSampleSize;
    }

    /**
     * 获取真实的路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件路径
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
     *
     * @param fileName 文件名称
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
     *
     * @param context 上下文
     * @param uri     uri
     * @return 文件名称
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
                Logger.e(TAG, "", e);
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

    private static void log(String tag, File file, Bitmap bitmap) {
        if (bitmap == null) {
            Logger.i(TAG, tag + ": path = " + file.getAbsolutePath() + ", size = " + (file.length() / 1024) + "KB");
        } else {
            Logger.i(TAG, tag + ": path = " + file.getAbsolutePath() + ", size = " + (file.length() / 1024) + "KB, width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());
        }
    }
}
