package com.fpliu.newton.ui.image.picker.source;

import com.fpliu.newton.ui.image.bean.ImageSet;

import java.util.List;

public interface LoadDataSourceListener {

    void onLoading(String path);

    void onLoaded(List<ImageSet> imageSetList);
}
