package com.fpliu.newton.ui.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.TextUtils;

import com.fpliu.newton.log.Logger;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类
 *
 * @author 792793182@qq.com 2015-06-17
 */
public final class ImageUtil {

    private static final String TAG = ImageUtil.class.getSimpleName();

    private ImageUtil() {
    }

    /**
     * Exif（Exchangeable Image File，可交换图像文件）是一种图像文件格式，它的数据存储与JPEG格式是完全相同的。
     * 实际上，Exif格式就是在JPEG格式头部插入了数码照片的信息，包括拍摄时的光圈、快门、白平衡、ISO、焦距、日期时间等
     * 各种和拍摄条件以及相机品牌、型号、色彩编码、拍摄时录制的声音以及全球定位系统（GPS）、缩略图等。
     * 简单地说，Exif=JPEG+拍摄参数。因此，你可以利用任何可以查看JPEG文件的看图软件浏览Exif格式的照片，
     * 但并不是所有的图形程序都能处理Exif信息。
     *
     * @param imageFilePath 图片路径
     * @return
     * @throws IOException
     */
    public static ExifInterface getExifInterface(String imageFilePath) throws IOException {
        return new ExifInterface(imageFilePath);
    }

    /**
     * 获取图片的宽度和高度
     *
     * @param imageFilePath 图片的路径
     * @return (width, height)，分别为宽度和高度
     */
    public static Point getWidthAndHeight(String imageFilePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 不去真的解析图片，只获取图片头部信息
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageFilePath, options);

        int width = options.outWidth;
        int height = options.outHeight;

        return new Point(width, height);
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // API 19
            return bitmap.getAllocationByteCount();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {// API 12
            return bitmap.getByteCount();
        }

        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 获取图片的宽度和高度
     *
     * @param is 图片的文件流
     * @return (width, height)，分别为宽度和高度
     */
    public static Point getWidthAndHeight(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 不去真的解析图片，只获取图片头部信息
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(is, null, options);

        int width = options.outWidth;
        int height = options.outHeight;

        return new Point(width, height);
    }

    /**
     * 图片缩放
     *
     * @param bitmap    目标图片
     * @param desWidth  目标宽度
     * @param desHeight 目标高度
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int desWidth, int desHeight) {
        return zoomBitmap(bitmap, (float) desWidth / bitmap.getWidth(),
                (float) desHeight / bitmap.getHeight());
    }

    /**
     * 图片缩放
     *
     * @param bitmap 目标图片
     * @param sx     水平方向的缩放比例
     * @param sy     竖直方向的缩放比例
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float sx, float sy) {
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    public static byte[] compress(Bitmap bitmap, Bitmap.CompressFormat format) {
        return compress(bitmap, 75, format);
    }

    public static byte[] compress(Bitmap bitmap, int quality, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos);
        return baos.toByteArray();
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath, Bitmap.CompressFormat format) {
        return saveBitmapToFile(bitmap, filePath, 75, format);
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap   位图
     * @param filePath 文件名，路径自己选择
     * @return 完整的路径
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath, int quality, Bitmap.CompressFormat format) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(compress(bitmap, quality, format));
            return true;
        } catch (Exception e) {
            Logger.e(TAG, "saveBitmapToFile()", e);
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Logger.e(TAG, "saveBitmapToFile()", e);
                }
            }
        }
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmapRes   位图
     * @param desFilePath 存放路径，路径自己选择
     * @return 完整的路径
     */
    public static boolean saveBitmapResToFile(Context context, int bitmapRes, String desFilePath, int quality, Bitmap.CompressFormat format) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(bitmapRes);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        saveBitmapToFile(bitmap, desFilePath, quality, Bitmap.CompressFormat.PNG);
        return true;
    }

    public static Bitmap toBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public static Bitmap getOneFrame(String filePath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(filePath);
        return media.getFrameAtTime();
    }

    public static File compressImageAndSaveBitmap(File oldfile) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        Bitmap image = BitmapFactory.decodeFile(oldfile.getPath(), options);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap image = BitmapFactory.decodeFile(oldfile.getPath(), options);
        options.inSampleSize = computeSampleSie(options, -1, 1920 * 1080);
        options.inJustDecodeBounds = false;
        image = BitmapFactory.decodeFile(oldfile.getPath(), options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int optionss = 100;
        while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于sizekb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            optionss -= 5;// 每次都减少5
            image.compress(Bitmap.CompressFormat.JPEG, optionss, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        FileOutputStream fos = null;
        try {
            String basePath = "";
            if (TextUtils.isEmpty(basePath)) {
                return oldfile;
            }
            File baseFile = new File(basePath);

            if (baseFile == null || !baseFile.canWrite() || !baseFile.canRead()) {
                return oldfile;
            }
            File newfile = new File(baseFile, oldfile.getName());
            fos = new FileOutputStream(newfile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            return newfile;
        } catch (Exception e) {
            return oldfile;
        }
    }

    /**
     * 压缩
     *
     * @param oldFile
     * @return
     */
    public static File compress(File oldFile) {
        String newPath = compress(oldFile.getPath());
        if (TextUtils.isEmpty(newPath)) {
            return oldFile;
        } else {
            return new File(newPath);
        }
    }

    /**
     * 压缩图片，返回地址
     *
     * @param oldPath
     * @return
     */
    public static String compress(String oldPath) {
        String basePath = "";
        if (TextUtils.isEmpty(basePath)) {
            return oldPath;
        }
        File baseFile = new File(basePath);

        if (baseFile == null || !baseFile.canWrite() || !baseFile.canRead()) {
            return oldPath;
        }
        File file = new File(oldPath);
        if (!file.exists()) {
            return "";
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
        options.inSampleSize = computeSampleSie(options, -1,
                1920 * 1080);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(oldPath, options);
        try {
            file = new File(baseFile, file.getName());
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            return file.getPath();
        } catch (Exception e) {
        }
        return oldPath;
    }

    public static int computeSampleSie(BitmapFactory.Options options, int minSideLength,
                                       int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
