package com.fpliu.newton.ui.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

import java.io.File;
import java.util.ArrayList;

/**
 * @author 792793182@qq.com 2016-07-12.
 */
public final class FilePreviewActivity extends BaseActivity {

    public static final String KEY_POSITION = "position";

    public static final String KEY_IMAGES = "images";

    private int position;

    private ArrayList<File> images;

    public static void start(Activity activity, int initPosition, ArrayList<File> images) {
        if (images == null || images.isEmpty()) {
            UIUtil.makeToast(activity, "照片路径为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, FilePreviewActivity.class);
        intent.putExtra(KEY_POSITION, initPosition);
        intent.putExtra(KEY_IMAGES, images);
        activity.startActivity(intent);
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
            images = (ArrayList<File>) getIntent().getSerializableExtra(KEY_IMAGES);
            position = getIntent().getIntExtra(KEY_POSITION, 0);
        } else {
            images = (ArrayList<File>) savedInstanceState.getSerializable(KEY_IMAGES);
            position = savedInstanceState.getInt(KEY_POSITION);
        }

        setTitle((position + 1) + "/" + images.size());

        ViewPager viewPager = new ViewPager(this);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                FilePreviewActivity.this.position = position;
                int size = images.size();
                setTitle((position % size + 1) + "/" + size);
            }
        });
        viewPager.setAdapter(new ViewPagerAdapter<File>(images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = ViewHolder.getInstance(R.layout.preview_view_item, convertView, parent);
                TouchImageView imageView = viewHolder.id(R.id.preview_view_item_image_view).getView();
                ImageManager.getImageLoader().onLoad(imageView, Uri.fromFile(new File(getItem(position).getPath())).toString(), R.drawable.btn_back_normal);
                return viewHolder.getItemView();
            }
        });
        viewPager.setCurrentItem(position, true);
        addContentView(viewPager);
    }
}
