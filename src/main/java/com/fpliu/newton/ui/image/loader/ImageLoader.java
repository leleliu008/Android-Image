package com.fpliu.newton.ui.image.loader;

import android.content.Context;
import android.widget.ImageView;

public interface ImageLoader {

    /**
     * 显示图片 - 原形
     *
     * @param context    上下文
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     * @param defaultImg 默认图片
     */
    void displayImage(Context context, ImageView imageView, String resource, int defaultImg);

    /**
     * 显示图片 - 原形
     *
     * @param context   上下文
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     */
    void displayImage(Context context, ImageView imageView, String resource);

    /**
     * 显示图片 - 原形
     *
     * @param defaultImg 默认图片
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     */
    void displayImage(ImageView imageView, String resource, int defaultImg);

    /**
     * 显示图片 - 原形
     *
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     */
    void displayImage(ImageView imageView, String resource);

    /**
     * 显示图片 - 原形
     *
     * @param context   上下文
     * @param resId     drawable资源ID
     * @param imageView 显示的控件
     */
    void displayImage(Context context, ImageView imageView, int resId);

    /**
     * 显示图片 - 圆形
     *
     * @param context    上下文
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     * @param defaultImg 默认图片
     */
    void displayCircleImage(Context context, ImageView imageView, String resource, int defaultImg);

    /**
     * 显示图片 - 圆形
     *
     * @param context   上下文
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     */
    void displayCircleImage(Context context, ImageView imageView, String resource);

    /**
     * 显示图片 - 圆形
     *
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     * @param defaultImg 默认图片
     */
    void displayCircleImage(ImageView imageView, String resource, int defaultImg);

    /**
     * 显示图片 - 圆形
     *
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     */
    void displayCircleImage(ImageView imageView, String resource);

    /**
     * 显示图片 - 圆角矩形
     *
     * @param context    上下文
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     * @param defaultImg 默认图片
     * @param radius     弧度
     */
    void displayRoundImage(Context context, ImageView imageView, String resource, int defaultImg, int radius);

    /**
     * 显示图片 - 圆角矩形
     *
     * @param context   上下文
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     * @param radius    弧度
     */
    void displayRoundImage(Context context, ImageView imageView, String resource, int radius);

    /**
     * 显示图片 - 圆角矩形
     *
     * @param resource   文件路径、uri、url都可以
     * @param imageView  显示的控件
     * @param defaultImg 默认图片
     * @param radius     弧度
     */
    void displayRoundImage(ImageView imageView, String resource, int defaultImg, int radius);

    /**
     * 显示图片 - 圆角矩形
     *
     * @param resource  文件路径、uri、url都可以
     * @param imageView 显示的控件
     * @param radius    弧度
     */
    void displayRoundImage(ImageView imageView, String resource, int radius);
}
