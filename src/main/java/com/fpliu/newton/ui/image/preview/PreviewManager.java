package com.fpliu.newton.ui.image.preview;


import android.app.Activity;

import com.fpliu.newton.ui.image.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;

public final class PreviewManager {

    private PreviewManager() {
    }

    public static void startFilePreview(Activity activity, int initPosition, ArrayList<File> images) {
        FilePreviewActivity.start(activity, initPosition, images);
    }

    public static void startUriPreview(Activity activity, int initPosition, ArrayList<String> images) {
        UriPreviewActivity.start(activity, initPosition, images);
    }

    public static void startImageItemPreview(Activity activity, int initPosition, ArrayList<ImageItem> images) {
        ImageItemPreviewActivity.start(activity, initPosition, images);
    }

    public static void startFilePreview(Activity activity, int initPosition, File imageFile) {
        ArrayList<File> images = new ArrayList<>();
        images.add(imageFile);
        FilePreviewActivity.start(activity, initPosition, images);
    }

    public static void startUriPreview(Activity activity, int initPosition, String imageFilePath) {
        ArrayList<String> images = new ArrayList<>();
        images.add(imageFilePath);
        UriPreviewActivity.start(activity, initPosition, images);
    }

    public static void startImageItemPreview(Activity activity, int initPosition, ImageItem imageItem) {
        ArrayList<ImageItem> images = new ArrayList<>();
        images.add(imageItem);
        ImageItemPreviewActivity.start(activity, initPosition, images);
    }
}
