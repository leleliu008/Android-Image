package com.fpliu.newton.ui.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fpliu.newton.ui.base.BaseActivity;
import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.ImageManager;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.ViewPagerAdapter;
import com.fpliu.newton.ui.image.view.TouchImageView;
import com.fpliu.newton.ui.list.ViewHolder;

import java.util.ArrayList;

/**
 * @author 792793182@qq.com 2016-07-12.
 */
public final class UriPreviewActivity extends BaseActivity {

    private static final String KEY_POSITION = "position";

    private static final String KEY_IMAGES = "images";

    private int position;

    private ArrayList<String> images;

    public static void start(Activity activity, int initPosition, ArrayList<String> images) {
        if (images == null || images.isEmpty()) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, UriPreviewActivity.class);
        intent.putExtra(KEY_POSITION, initPosition);
        intent.putExtra(KEY_IMAGES, images);
        activity.startActivity(intent);
    }


    public static void start(Activity activity, String imageURI) {
        if (TextUtils.isEmpty(imageURI)) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> images = new ArrayList<>(1);
        images.add(imageURI);
        start(activity, 0, images);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_POSITION, position);
        outState.putSerializable(KEY_IMAGES, images);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            images = (ArrayList<String>) getIntent().getSerializableExtra(KEY_IMAGES);
            position = getIntent().getIntExtra(KEY_POSITION, 0);
        } else {
            images = (ArrayList<String>) savedInstanceState.getSerializable(KEY_IMAGES);
            position = savedInstanceState.getInt(KEY_POSITION, 0);
        }

        setTitle((position + 1) + "/" + images.size());

        ViewPager viewPager = new ViewPager(this);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                position = i;
                int size = images.size();
                setTitle((position % size + 1) + "/" + size);
            }
        });
        viewPager.setAdapter(new ViewPagerAdapter<String>(images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = ViewHolder.getInstance(R.layout.preview_view_item, convertView, parent);
                TouchImageView imageView = viewHolder.id(R.id.preview_view_item_image_view).getView();
                ImageManager.getImageLoader().onLoad(imageView, getItem(position), R.drawable.btn_back_normal);
                return viewHolder.getItemView();
            }
        });
        viewPager.setCurrentItem(position, true);
        addContentView(viewPager);
    }
}
