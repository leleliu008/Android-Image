package com.fpliu.newton.ui.image.preview;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.fpliu.newton.ui.base.BaseActivity;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.ViewPagerAdapter;
import com.fpliu.newton.ui.list.ViewHolder;

import java.util.ArrayList;

abstract class BasePreviewActivity<T> extends BaseActivity {

    static final String KEY_POSITION = "position";

    static final String KEY_IMAGES = "images";

    private int position;

    private ArrayList<T> images;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_POSITION, position);
        outState.putSerializable(KEY_IMAGES, images());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            position = getIntent().getIntExtra(KEY_POSITION, 0);
            images = (ArrayList<T>) getIntent().getSerializableExtra(KEY_IMAGES);
        } else {
            position = savedInstanceState.getInt(KEY_POSITION);
            images = (ArrayList<T>) savedInstanceState.getSerializable(KEY_IMAGES);
        }

        setTitle((position + 1) + "/" + images.size());

        ViewPager viewPager = new ViewPager(this);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                BasePreviewActivity.this.position = position;
                int size = images.size();
                setTitle((position % size + 1) + "/" + size);
            }
        });
        viewPager.setAdapter(new ViewPagerAdapter<T>(images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return ViewHolder.getInstance(R.layout.preview_view_item, convertView, parent)
                        .id(R.id.preview_view_item_image_view).image(getUri(position, getItem(position)), R.drawable.btn_back_normal)
                        .getItemView();
            }
        });
        viewPager.setCurrentItem(position, true);
        addViewInBody(viewPager);
    }

    protected final ArrayList<T> images() {
        return images;
    }

    protected final void images(ArrayList<T> images) {
        this.images = images;
    }

    protected abstract String getUri(int position, T item);
}
