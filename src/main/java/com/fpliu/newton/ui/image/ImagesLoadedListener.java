package com.fpliu.newton.ui.image;

import com.fpliu.newton.ui.image.bean.ImageSet;

import java.util.List;

public interface ImagesLoadedListener {
    void onImagesLoaded(List<ImageSet> imageSetList);
}
