package com.fpliu.newton.ui.image.picker;

import android.app.Activity;
import android.content.Intent;

import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.bean.ImageSet;
import com.fpliu.newton.ui.image.crop.ImageCropCompleteListener;
import com.fpliu.newton.ui.image.picker.source.DataSource;
import com.fpliu.newton.ui.image.picker.source.LocalDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImagePicker {

    /**
     * 单选模式
     */
    static final int SELECT_MODE_SINGLE = 0;

    /**
     * 多选模式
     */
    static final int SELECT_MODE_MULTI = 1;

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
     * 数据源过滤器
     */
    private List<String> dataSourceFilters;

    /**
     * 选择完成的回掉
     */
    private ImagePickCompleteListener imagePickCompleteListener;

    /**
     * 裁减完成的回掉
     */
    private ImageCropCompleteListener imageCropCompleteListener;

    /**
     * 选择的图片列表
     */
    private List<ImageItem> pickedImages = new ArrayList<>();

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

    public ImagePicker pickedImages(List<ImageItem> pickedImages) {
        this.pickedImages = pickedImages;
        return this;
    }

    public List<ImageItem> pickedImages() {
        return pickedImages;
    }

    public int pickedImageCount() {
        return pickedImages == null ? 0 : pickedImages.size();
    }

    public boolean isPicked(ImageItem item) {
        return pickedImages.contains(item);
    }

    public boolean addInPickedCache(ImageItem item) {
        if (isPicked(item)) {
            return true;
        } else {
            return pickedImages.add(item);
        }
    }

    public boolean deleteFromPickedCache(ImageItem item) {
        return pickedImages.remove(item);
    }

    public void clearPickedCache() {
        if (pickedImages != null) {
            pickedImages.clear();
        }
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

    public ImagePicker dataSourceFilters(String... filters) {
        dataSourceFilters = Arrays.asList(filters);
        return this;
    }

    public ImagePicker dataSourceFilters(List<String> filters) {
        dataSourceFilters = filters;
        return this;
    }

    public List<String> dataSourceFilters() {
        return dataSourceFilters;
    }

    public ImagePicker imageCropCompleteListener(ImageCropCompleteListener listener) {
        this.imageCropCompleteListener = listener;
        return this;
    }

    public ImageCropCompleteListener imageCropCompleteListener() {
        return imageCropCompleteListener;
    }

    public ImagePicker imagePickCompleteListener(ImagePickCompleteListener listener) {
        this.imagePickCompleteListener = listener;
        return this;
    }

    public ImagePickCompleteListener imagePickCompleteListener() {
        return imagePickCompleteListener;
    }

    public ImagePicker pick(Activity activity) {
        if (dataSource instanceof LocalDataSource) {
            String packageName = activity.getPackageName();
            String filter1 = "/sdcard/" + packageName;
            String filter2 = "/storage/emulated/0/" + packageName;
            if (dataSourceFilters == null) {
                dataSourceFilters = new ArrayList<>();
            }
            dataSourceFilters.add(filter1);
            dataSourceFilters.add(filter2);
        }
        activity.startActivity(new Intent(activity, ImageGridActivity.class));
        return this;
    }


    //------------------------------------------internal---------------------------------------------//
    private List<ImageSet> imageSets;

    void setImageSets(List<ImageSet> imageSets) {
        this.imageSets = imageSets;
    }

    List<ImageSet> getImageSets() {
        return imageSets;
    }

    void clearImageSets() {
        if (imageSets != null) {
            imageSets.clear();
            imageSets = null;
        }
    }

    public List<ImageItem> getImageItemsOfImageSet(int position) {
        return imageSets == null ? null : imageSets.get(position).imageItems;
    }

    void reset() {
        clearImageSets();
        clearPickedCache();
        needShowCamera = true;
        needCrop = false;
        cropWidth = 240;
        cropHeight = 240;
        selectMode = SELECT_MODE_MULTI;
        maxSelectCount = 9;
        if (!(dataSource instanceof LocalDataSource)) {
            dataSource = new LocalDataSource();
        }
        dataSourceFilters = null;
        imageCropCompleteListener = null;
        imagePickCompleteListener = null;
    }

    void notifyImagePickComplete() {
        if (imagePickCompleteListener != null) {
            List<ImageItem> list = new ArrayList<>();
            list.addAll(pickedImages());
            imagePickCompleteListener.onImagePickComplete(list);
            reset();
        }
    }
}
