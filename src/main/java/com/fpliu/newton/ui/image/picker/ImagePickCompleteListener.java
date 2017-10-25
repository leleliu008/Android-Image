package com.fpliu.newton.ui.image.picker;

import com.fpliu.newton.ui.image.bean.ImageItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author 792793182@qq.com 2017-08-11.
 */
public interface ImagePickCompleteListener extends Serializable {
    void onImagePickComplete(List<ImageItem> items);
}
