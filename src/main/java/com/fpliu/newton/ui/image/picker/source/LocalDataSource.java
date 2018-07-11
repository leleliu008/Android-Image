package com.fpliu.newton.ui.image.picker.source;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.fpliu.newton.log.Logger;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.bean.ImageSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDataSource implements DataSource {

    private static final String TAG = LocalDataSource.class.getSimpleName();

    private static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    public static final int LOADER_ALL = 0;

    @Override
    public void loadData(final Context context, final LoadDataSourceListener listener) {
        if (context instanceof FragmentActivity) {
            ((FragmentActivity) context).getSupportLoaderManager().initLoader(LOADER_ALL, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Logger.d(TAG, "onCreateLoader() id = " + id);
                    if (id == LOADER_ALL) {
                        return new CursorLoader(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                null, null, IMAGE_PROJECTION[2] + " DESC");
                    } else {
                        return new CursorLoader(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    Logger.d(TAG, "onLoadFinished()");
                    if (data == null) {
                        listener.onLoaded(null);
                        return;
                    }

                    int count = data.getCount();
                    if (count <= 0) {
                        listener.onLoaded(null);
                        return;
                    }

                    ArrayList<ImageSet> imageSetList = new ArrayList<>();
                    List<ImageItem> imageItems = new ArrayList<>();

                    data.moveToFirst();
                    do {
                        String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));

                        //如果文件不存在，就忽略掉
                        if (!new File(imagePath).exists()) {
                            continue;
                        }

                        if (!listener.filter(imagePath)) {
                            continue;
                        }

                        String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                        ImageItem item = new ImageItem(imagePath, imageName, imageAddedTime);
                        imageItems.add(item);

                        File imageFile = new File(imagePath);
                        File imageParentFile = imageFile.getParentFile();

                        ImageSet imageSet = new ImageSet();
                        imageSet.name = imageParentFile.getName();
                        imageSet.path = imageParentFile.getAbsolutePath();
                        imageSet.cover = item;

                        if (imageSetList.contains(imageSet)) {
                            imageSetList.get(imageSetList.indexOf(imageSet)).imageItems.add(item);
                        } else {
                            List<ImageItem> imageList = new ArrayList<>();
                            imageList.add(item);
                            imageSet.imageItems = imageList;
                            imageSetList.add(imageSet);
                        }
                    } while (data.moveToNext());


                    ImageSet imageSetAll = new ImageSet();
                    imageSetAll.name = context.getResources().getString(R.string.ai_all_images);
                    if (imageItems.isEmpty()) {
                        imageSetAll.cover = new ImageItem("", "", 0);
                    } else {
                        imageSetAll.cover = imageItems.get(0);
                    }
                    imageSetAll.imageItems = imageItems;
                    imageSetAll.path = "/";

                    if (imageSetList.contains(imageSetAll)) {
                        imageSetList.remove(imageSetAll);
                    }
                    imageSetList.add(0, imageSetAll);

                    listener.onLoaded(imageSetList);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    Logger.d(TAG, "onLoaderReset()");
                }
            });
        } else {
            throw new RuntimeException("your activity must be instance of FragmentActivity");
        }
    }
}
