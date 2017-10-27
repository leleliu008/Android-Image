package com.fpliu.newton.ui.image.preview;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fpliu.newton.ui.base.BaseActivity;
import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.ViewPagerAdapter;
import com.fpliu.newton.ui.image.view.TouchImageView;
import com.fpliu.newton.ui.list.ViewHolder;

import java.util.ArrayList;

abstract class BasePreviewActivity<T> extends BaseActivity implements View.OnClickListener {

    static final String KEY_POSITION = "position";

    static final String KEY_IMAGES = "images";

    private int position;

    private ArrayList<T> images;

    private TextView textView;

    private int screenHeight;

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

        screenHeight = UIUtil.getScreenHeight(this);

        ViewPager viewPager = new ViewPager(this);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                BasePreviewActivity.this.position = position;
                updatePosition();
            }
        });
        viewPager.setAdapter(new ViewPagerAdapter<T>(images) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder = ViewHolder.getInstance(R.layout.preview_view_item, convertView, parent);
                TouchImageView touchImageView = viewHolder.id(R.id.preview_view_item_image_view)
                        .image(getUri(position, getItem(position)), R.drawable.shape_black_bg)
                        .tagWithCurrentId("TouchImageView")
                        .clicked(BasePreviewActivity.this)
                        .getView();
                touchImageView.setMaxHeight(screenHeight);
                return viewHolder.getItemView();
            }
        });
        viewPager.setCurrentItem(position, true);
        setContentView(viewPager);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        lp.bottomMargin = (int) getResources().getDimension(R.dimen.dp750_30);

        textView = new TextView(this);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        addContentView(textView, lp);

        updatePosition();
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            if (tag instanceof String) {
                String tagStr = (String) tag;
                if ("TouchImageView".equals(tagStr)) {
                    finish();
                }
            }
        }
    }

    private void updatePosition() {
        if (textView != null) {
            textView.setText((position + 1) + "/" + images.size());
        }
    }

    protected final ArrayList<T> images() {
        return images;
    }

    protected final void images(ArrayList<T> images) {
        this.images = images;
    }

    protected abstract String getUri(int position, T item);
}
