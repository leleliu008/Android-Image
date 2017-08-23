package com.fpliu.newton.ui.image.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.fpliu.newton.log.Logger;
import com.fpliu.newton.ui.image.ImageManager;
import com.fpliu.newton.ui.image.ImageSelectedChangeListener;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.ViewPagerAdapter;
import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.view.TouchImageView;

import java.io.File;

public class ImagePreviewActivity extends FragmentActivity
        implements View.OnClickListener, ImageSelectedChangeListener, GestureDetector.OnDoubleTapListener {

    private static final String TAG = ImagePreviewActivity.class.getSimpleName();

    private ImageManager androidImagePicker;
    private TextView mTitleCount;
    private CheckBox mCbSelected;
    private TextView mBtnOk;

    private int setPosition;
    private int picPosition;


    public static void startForResult(Activity parent, int requestCode, int setPosition, int picPosition) {
        Intent intent = new Intent(parent, ImagePreviewActivity.class);
        intent.putExtra(ImageManager.KEY_SET_SELECTED_POSITION, setPosition);
        intent.putExtra(ImageManager.KEY_PIC_SELECTED_POSITION, picPosition);
        parent.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ImageManager.KEY_SET_SELECTED_POSITION, setPosition);
        outState.putInt(ImageManager.KEY_PIC_SELECTED_POSITION, picPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setPosition = getIntent().getIntExtra(ImageManager.KEY_SET_SELECTED_POSITION, 0);
            picPosition = getIntent().getIntExtra(ImageManager.KEY_PIC_SELECTED_POSITION, 0);
        } else {
            setPosition = savedInstanceState.getInt(ImageManager.KEY_SET_SELECTED_POSITION, 0);
            picPosition = savedInstanceState.getInt(ImageManager.KEY_PIC_SELECTED_POSITION, 0);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pre);

        androidImagePicker = ImageManager.getInstance();
        androidImagePicker.addOnImageSelectedChangeListener(this);

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);

        mCbSelected = (CheckBox) findViewById(R.id.btn_check);
        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText("1/" + androidImagePicker.getImageItemsOfImageSet(setPosition).size());

        int selectedCount = androidImagePicker.getSelectImageCount();

        onImageSelectChange(0, null, selectedCount, androidImagePicker.maxSelectCount());

        //back press
        findViewById(R.id.btn_backpress).setOnClickListener(v -> finish());

        mCbSelected.setOnClickListener(v -> {
            if (androidImagePicker.getSelectImageCount() > androidImagePicker.maxSelectCount()) {
                if (mCbSelected.isChecked()) {
                    mCbSelected.toggle();
                    String toast = getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.maxSelectCount());
                    Toast.makeText(ImagePreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> selectCurrent(isChecked));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter<ImageItem>(androidImagePicker.getImageItemsOfImageSet(setPosition)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TouchImageView imageView = new TouchImageView(parent.getContext());
                imageView.setBackgroundColor(0xff000000);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
                imageView.setOnDoubleTapListener(ImagePreviewActivity.this);

                ImageManager.getImageLoader().onLoad(imageView, Uri.fromFile(new File(getItem(position).path)).toString(), R.drawable.default_img);

                return imageView;
            }
        });
        viewPager.setCurrentItem(picPosition, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                picPosition = position % androidImagePicker.getImageItemsOfImageSet(setPosition).size();
                boolean isSelected = false;
                ImageItem item = androidImagePicker.getImageItemsOfImageSet(setPosition).get(picPosition);
                if (androidImagePicker.isSelect(position, item)) {
                    isSelected = true;
                }
                mTitleCount.setText(picPosition + 1 + "/" + androidImagePicker.getImageItemsOfImageSet(setPosition).size());
                mCbSelected.setChecked(isSelected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            setResult(RESULT_OK);
            finish();
        } else if (v.getId() == R.id.btn_pic_rechoose) {
            finish();
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Logger.d(TAG, "onImageSingleTap()");

        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        Logger.d(TAG, "onImageSelectChange()");

        if (selectedItemsCount > 0) {
            mBtnOk.setEnabled(true);
            mBtnOk.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        Logger.d(TAG, "removeOnImageItemSelectedChangeListener");
        super.onDestroy();
    }

    /**
     * public method:select the current show image
     */
    public void selectCurrent(boolean isCheck) {
        ImageItem item = androidImagePicker.getImageItemsOfImageSet(setPosition).get(picPosition);
        boolean isSelect = androidImagePicker.isSelect(picPosition, item);
        if (isCheck) {
            if (!isSelect) {
                ImageManager.getInstance().addSelectedImageItem(picPosition, item);
            }
        } else {
            if (isSelect) {
                ImageManager.getInstance().deleteSelectedImageItem(picPosition, item);
            }
        }
    }
}
