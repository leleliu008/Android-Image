package com.fpliu.newton.ui.image.picker.source;

import android.content.Context;

import java.util.List;

/**
 * 数据源，可以来自本地数据库、文件，也可以来自网络
 */
public interface DataSource {
    /**
     * 加载数据接口
     *
     * @param context  上下文
     * @param filters  过滤掉的
     * @param listener 加载完成的回掉
     */
    void loadData(Context context, List<String> filters, LoadDataSourceListener listener);
}
