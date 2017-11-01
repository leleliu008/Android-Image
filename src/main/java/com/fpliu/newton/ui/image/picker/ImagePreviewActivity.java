package com.fpliu.newton.ui.image.picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.preview.BasePreviewActivity;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends BasePreviewActivity<ImageItem> {

    private static final String KEY_SET_SELECTED_POSITION = "key_set_selected";

    //集合的序号，如果为-1，表示是预览所有已经选择好的，否则就是预览全部
    private int setPosition;
    private int picPosition;

    public static void startForResult(Activity parent, int requestCode, int setPosition, int picPosition) {
        Intent intent = new Intent(parent, ImagePreviewActivity.class);
        intent.putExtra(KEY_SET_SELECTED_POSITION, setPosition);
        intent.putExtra(KEY_POSITION, picPosition);
        parent.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SET_SELECTED_POSITION, setPosition);
        outState.putInt(KEY_POSITION, picPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setPosition = getIntent().getIntExtra(KEY_SET_SELECTED_POSITION, 0);
            picPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        } else {
            setPosition = savedInstanceState.getInt(KEY_SET_SELECTED_POSITION, 0);
            picPosition = savedInstanceState.getInt(KEY_POSITION, 0);
        }

        ImagePicker imagePicker = ImagePicker.getInstance();
        List<ImageItem> imageItems;
        if (setPosition < 0) {
            imageItems = imagePicker.pickedImages();
        } else {
            imageItems = imagePicker.getImageItemsOfImageSet(setPosition);
        }
        images((ArrayList<ImageItem>) imageItems);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getUri(int position, ImageItem item) {
        return item.path;
    }
}
