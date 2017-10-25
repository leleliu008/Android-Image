package com.fpliu.newton.ui.image.picker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.bean.ImageSet;
import com.fpliu.newton.ui.image.crop.CropCompleteListener;
import com.fpliu.newton.ui.image.loader.DefaultImageLoader;
import com.fpliu.newton.ui.image.loader.ImageLoader;
import com.fpliu.newton.ui.image.picker.source.DataSource;
import com.fpliu.newton.ui.image.picker.source.LocalDataSource;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class ImagePicker {

    public static final String KEY_PIC_PATH = "key_pic_path";

    public static final String KEY_SET_SELECTED_POSITION = "key_set_selected";

    public static final String KEY_PIC_SELECTED_POSITION = "key_pic_selected";

    /**
     * 单选模式
     */
    static final int SELECT_MODE_SINGLE = 0;
    /**
     * 多选模式
     */
    static final int SELECT_MODE_MULTI = 1;


    private static final String TAG = ImagePicker.class.getSimpleName();

    /**
     * 是否显示拍照的按钮
     */
    private boolean needShowCamera = true;

    /**
     * 选择完成或者拍照完成后，是否需要进行裁剪
     */
    private boolean needCrop = false;

    /**
     * 裁剪宽度
     */
    private int cropWidth = 240;

    /**
     * 裁剪高度
     */
    private int cropHeight = 240;

    /**
     * 选择模式：单选或者多选
     */
    private int selectMode = SELECT_MODE_MULTI;

    /**
     * 多选模式下，最多可以选择的图片数量，默认最多选择9张
     */
    private int maxSelectCount = 9;

    /**
     * 数据源
     */
    private DataSource dataSource = new LocalDataSource();

    /**
     * 选择完成的回掉
     */
    private ImagePickCompleteListener onImagePickCompleteListener;


    private static ImageLoader imageLoader = new DefaultImageLoader();

    private static ImagePicker mInstance;

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        ImagePicker.imageLoader = imageLoader;
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    public ImagePicker maxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
        return this;
    }

    public int maxSelectCount() {
        return maxSelectCount;
    }

    ImagePicker selectMode(int selectMode) {
        this.selectMode = selectMode;
        return this;
    }

    public boolean isSingleMode() {
        return selectMode == SELECT_MODE_SINGLE;
    }

    public ImagePicker singleMode() {
        selectMode = SELECT_MODE_SINGLE;
        return this;
    }

    public ImagePicker multiMode() {
        selectMode = SELECT_MODE_MULTI;
        return this;
    }

    public boolean isMultiMode() {
        return selectMode == SELECT_MODE_MULTI;
    }

    public ImagePicker needShowCamera(boolean needShowCamera) {
        this.needShowCamera = needShowCamera;
        return this;
    }

    public boolean needShowCamera() {
        return needShowCamera;
    }

    public ImagePicker needCrop(boolean needCrop) {
        this.needCrop = needCrop;
        return this;
    }

    public boolean needCrop() {
        return needCrop;
    }

    public ImagePicker cropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
        return this;
    }

    public int cropWidth() {
        return cropWidth;
    }

    public ImagePicker cropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
        return this;
    }

    public int cropHeight() {
        return cropHeight;
    }

    public ImagePicker dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DataSource dataSource() {
        return dataSource;
    }

    public ImagePicker onImagePickComplete(ImagePickCompleteListener listener) {
        this.onImagePickCompleteListener = listener;
        return this;
    }

    public ImagePicker pick(Activity activity) {
        activity.startActivity(new Intent(activity, ImagesGridActivity.class));
        return this;
    }

    /**
     * Listeners of image selected changes,if you want to custom the Activity of ImagesGrid or ImagePreview,you might use it.
     */
    private List<ImageSelectedChangeListener> mImageSelectedChangeListeners;

    public void addOnImageSelectedChangeListener(ImageSelectedChangeListener l) {
        if (mImageSelectedChangeListeners == null) {
            mImageSelectedChangeListeners = new ArrayList<>();
        }
        mImageSelectedChangeListeners.add(l);
    }

    public void removeOnImageItemSelectedChangeListener(ImageSelectedChangeListener listener) {
        if (mImageSelectedChangeListeners != null) {
            mImageSelectedChangeListeners.remove(listener);
        }
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if ((isAdd && getSelectImageCount() > maxSelectCount) || (!isAdd && getSelectImageCount() == maxSelectCount)) {
            //do not call the listeners if reached the select limit when selecting
            Log.i(TAG, "=====ignore notifyImageSelectedChanged:isAdd?" + isAdd);
        } else {
            if (mImageSelectedChangeListeners != null) {
                for (ImageSelectedChangeListener listener : mImageSelectedChangeListeners) {
                    if (listener != null) {
                        listener.onImageSelectChange(position, item, mSelectedImages.size(), maxSelectCount);
                    }
                }
            }
        }
    }

    private List<CropCompleteListener> mImageCropCompleteListeners;

    public void addOnImageCropCompleteListener(CropCompleteListener listener) {
        if (mImageCropCompleteListeners == null) {
            mImageCropCompleteListeners = new ArrayList<>();
        }
        mImageCropCompleteListeners.add(listener);
    }

    public void removeOnImageCropCompleteListener(CropCompleteListener listener) {
        if (mImageCropCompleteListeners != null) {
            mImageCropCompleteListeners.remove(listener);
        }
    }

    public void notifyImageCropComplete(Bitmap bmp, int ratio) {
        if (mImageCropCompleteListeners != null) {
            for (CropCompleteListener listener : mImageCropCompleteListeners) {
                if (listener != null) {
                    listener.onImageCropComplete(bmp, ratio);
                }
            }
        }
    }

    public void notifyOnImagePickComplete() {
        if (onImagePickCompleteListener != null) {
            onImagePickCompleteListener.onImagePickComplete(getSelectedImages());
        }
    }

    //All Images collect by Set
    private List<ImageSet> mImageSets;

    LinkedHashSet<ImageItem> mSelectedImages = new LinkedHashSet<ImageItem>();

    public List<ImageSet> getImageSets() {
        return mImageSets;
    }

    public List<ImageItem> getImageItemsOfImageSet(int position) {
        return mImageSets == null ? null : mImageSets.get(position).imageItems;
    }

    public void setImageSets(List<ImageSet> mImageSets) {
        this.mImageSets = mImageSets;
    }

    public void clearImageSets() {
        if (mImageSets != null) {
            mImageSets.clear();
            mImageSets = null;
        }
    }


    public void addSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.add(item);
        notifyImageSelectedChanged(position, item, true);
    }

    public void deleteSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.remove(item);
        notifyImageSelectedChanged(position, item, false);
    }

    public boolean isSelect(int position, ImageItem item) {
        return mSelectedImages.contains(item);
    }

    public int getSelectImageCount() {
        return mSelectedImages == null ? 0 : mSelectedImages.size();
    }

    public void onDestroy() {
        if (mImageSelectedChangeListeners != null) {
            mImageSelectedChangeListeners.clear();
            mImageSelectedChangeListeners = null;
        }
        if (mImageCropCompleteListeners != null) {
            mImageCropCompleteListeners.clear();
            mImageCropCompleteListeners = null;
        }

        clearImageSets();
    }

    public List<ImageItem> getSelectedImages() {
        List<ImageItem> list = new ArrayList<>();
        list.addAll(mSelectedImages);
        return list;
    }

    public void clearSelectedImages() {
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
    }
}
