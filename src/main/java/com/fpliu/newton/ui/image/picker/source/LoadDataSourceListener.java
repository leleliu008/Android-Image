package com.fpliu.newton.ui.image.picker.source;

import com.fpliu.newton.ui.image.bean.ImageSet;

import java.util.List;

public interface LoadDataSourceListener {

    /**
     *
     * @param path
     * @return true表示使用该结果，false表示过滤掉该结果
     */
    boolean onLoading(String path);

    void onLoaded(List<ImageSet> imageSetList);
}
