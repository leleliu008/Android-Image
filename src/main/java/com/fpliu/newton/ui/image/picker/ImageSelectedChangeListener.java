package com.fpliu.newton.ui.image.picker;

import com.fpliu.newton.ui.image.bean.ImageItem;

/**
 * @author 792793182@qq.com 2017-08-11.
 */
public interface ImageSelectedChangeListener {
    void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit);
}
