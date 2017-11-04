package com.fpliu.newton.ui.image.loader;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

/**
 * 默认的图片加载器
 */
public class DefaultImageLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, ImageView imageView, String resource, int defaultImg) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayImage(Context context, ImageView imageView, String resource) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayImage(ImageView imageView, String resource, int defaultImg) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayImage(ImageView imageView, String resource) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayImage(Context context, ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void displayImage(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void displayCircleImage(Context context, ImageView imageView, String resource, int defaultImg) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayCircleImage(Context context, ImageView imageView, String resource) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayCircleImage(ImageView imageView, String resource, int defaultImg) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayCircleImage(ImageView imageView, String resource) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayCircleImage(Context context, ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void displayCircleImage(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    @Override
    public void displayRoundImage(Context context, ImageView imageView, String resource, int defaultImg, int radius) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayRoundImage(Context context, ImageView imageView, String resource, int radius) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayRoundImage(ImageView imageView, String resource, int defaultImg, int radius) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayRoundImage(ImageView imageView, String resource, int radius) {
        imageView.setImageURI(Uri.parse(resource));
    }

    @Override
    public void displayRoundImage(Context context, ImageView imageView, int resId, int radius) {
        imageView.setImageResource(resId);
    }

    @Override
    public void displayRoundImage(ImageView imageView, int resId, int radius) {
        imageView.setImageResource(resId);
    }
}
