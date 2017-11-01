package com.fpliu.newton.ui.image.loader;

public final class ImageLoaderManager {

    private static ImageLoader imageLoader = new DefaultImageLoader();

    private ImageLoaderManager() {
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        if (imageLoader != null) {
            ImageLoaderManager.imageLoader = imageLoader;
        }
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
