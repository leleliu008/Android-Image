package com.fpliu.newton.ui.image.picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fpliu.newton.log.Logger;
import com.fpliu.newton.ui.base.TextBtn;
import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.Util;
import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.bean.ImageSet;
import com.fpliu.newton.ui.image.crop.ImageCropActivity;
import com.fpliu.newton.ui.image.loader.ImageLoaderManager;
import com.fpliu.newton.ui.image.picker.source.LoadDataSourceListener;
import com.fpliu.newton.ui.list.ItemAdapter;
import com.fpliu.newton.ui.list.PullableRecyclerViewActivity;
import com.fpliu.newton.ui.list.ViewHolder;
import com.fpliu.newton.ui.pullable.PullType;
import com.fpliu.newton.ui.pullable.PullableViewContainer;
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageGridActivity extends PullableRecyclerViewActivity<ImageItem, ItemViewHolder>
        implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ImageGridActivity.class.getSimpleName();

    private static final String KEY_FILE_PATH = "imagePath";

    private static final int REQUEST_CODE_CAMERA = 1000;

    private static final int REQUEST_CODE_PREVIEW = 1001;

    private static final int REQUEST_CODE_CROP = 1002;

    private ImagePicker imagePicker;

    private Button completeBtn;

    private TextView imageSetBtn;

    private TextView previewBtn;

    private View footerView;

    private ListPopupWindow mFolderPopupWindow;

    private ItemAdapter<ImageSet> itemAdapter;

    private int selectedImageSetIndex;

    //拍照的保存文件路径
    private String takePictureImagePath;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FILE_PATH, takePictureImagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imagePicker = ImagePicker.getInstance();
        if (savedInstanceState != null) {
            takePictureImagePath = savedInstanceState.getString(KEY_FILE_PATH);
        }

        super.onCreate(savedInstanceState);

        setTitle("图片");
        getPullableViewContainer().getPullableView().setBackgroundColor(Color.WHITE);

        asGrid(3);
        clearItemDecorations();
        canPullDown(false);
        canPullUp(false);

        //添加Footer
        setViewAfterBody(R.layout.images_grid_footer);
        footerView = findViewById(R.id.footer_panel);
        imageSetBtn = (TextView) findViewById(R.id.footer_panel_image_set_btn);
        click(imageSetBtn).subscribe(view -> {
            if (mFolderPopupWindow.isShowing()) {
                backgroundAlpha(1f);
                mFolderPopupWindow.dismiss();
            } else {
                backgroundAlpha(0.3f);
                mFolderPopupWindow.show();
                ListView listView = mFolderPopupWindow.getListView();
                listView.setSelection(selectedImageSetIndex);
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.divider)));
                listView.setDividerHeight(1);
            }
        });
        previewBtn = (TextView) findViewById(R.id.footer_panel_preview_picked_btn);

        //多选模式下，右边有一个"完成"按钮
        if (imagePicker.isMultiMode()) {
            getContentView().setRightViewStrategy(new TextBtn() {
                @Override
                public Button onCreateView(RelativeLayout headView) {
                    completeBtn = super.onCreateView(headView);
                    completeBtn.setEnabled(false);
                    completeBtn.setText("完成");
                    completeBtn.setTextSize(20);
                    completeBtn.setPadding(0, 0, 20, 0);
                    completeBtn.setTextColor(getResources().getColorStateList(R.color.color_selector));
                    return completeBtn;
                }
            }).getRightBtnClickObservable().compose(bindToLifecycle()).subscribe(o -> {
                imagePicker.notifyImagePickComplete();
                finish();
            });

            click(previewBtn).subscribe(view -> ImagePreviewActivity.startForResult(me(), REQUEST_CODE_PREVIEW, -1, 0));

            updateStatus();
        } else {
            previewBtn.setVisibility(View.GONE);
        }

        onRefreshOrLoadMore(null, null, 0, 0);
    }

    @Override
    public void onRefreshOrLoadMore(PullableViewContainer<RecyclerView> pullableViewContainer, PullType pullType, int pageNumber, int pageSize) {
        imagePicker.dataSource().loadData(me(), new LoadDataSourceListener() {
            @Override
            public boolean filter(String imagePath) {
                List<String> filters = imagePicker.dataSourceFilters();

                if (filters != null && !filters.isEmpty()) {
                    for (String filterFilePath : filters) {
                        if (imagePath.startsWith(filterFilePath)) {
                            Logger.d(TAG, imagePath + " is filtered!");
                            return false;
                        }
                    }
                }

                return true;
            }

            @Override
            public void onLoaded(List<ImageSet> imageSetList) {
                //说明没有图片
                if (imageSetList == null) {
                    if (imagePicker.needShowCamera()) {
                        List<ImageItem> imageItems = new ArrayList<>();
                        imageItems.add(null);
                        setItems(imageItems);
                    } else {
                        pullableViewContainer.finishRequest(pullType, true, "没有图片");
                    }
                } else {
                    imagePicker.setImageSets(imageSetList);

                    ImageSet imageSet = imageSetList.get(0);
                    List<ImageItem> imageItems = imageSet.imageItems;
                    if (imagePicker.needShowCamera()) {
                        imageItems = new ArrayList<>();
                        imageItems.add(null);
                        imageItems.addAll(imageSet.imageItems);
                    }
                    setItems(imageItems);

                    if (mFolderPopupWindow == null) {
                        createPopupFolderList(me());
                    }
                    imageSetBtn.setText(imageSet.name);
                    itemAdapter.setItems(imageSetList);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            imagePicker.reset();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLeftBtnClick() {
        imagePicker.reset();
        super.onLeftBtnClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从裁减界面返回
        if (requestCode == REQUEST_CODE_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
        //从拍照返回
        else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (!TextUtils.isEmpty(takePictureImagePath)) {
                    //保存到数据库中
                    Util.galleryAddPic(me(), takePictureImagePath);

                    //需要进行裁剪
                    if (imagePicker.needCrop()) {
                        finish();
                        ImageCropActivity.startForResult(REQUEST_CODE_CROP, me(), takePictureImagePath, imagePicker.cropWidth(), imagePicker.imageCropCompleteListener());
                    } else {
                        ImageItem item = new ImageItem(takePictureImagePath, "", -1);
                        imagePicker.clearPickedCache();
                        imagePicker.addInPickedCache(item);
                        imagePicker.notifyImagePickComplete();

                        setResult(RESULT_OK);
                        finish();
                    }
                }
            }
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(Context context) {
        mFolderPopupWindow = new ListPopupWindow(context);
        mFolderPopupWindow.setContentWidth(UIUtil.getScreenWidth(context));
        mFolderPopupWindow.setWidth(UIUtil.getScreenWidth(context));
        mFolderPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mFolderPopupWindow.setAnchorView(footerView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnDismissListener(() -> backgroundAlpha(1f));
        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        mFolderPopupWindow.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedImageSetIndex = position;
            new Handler().postDelayed(() -> {
                mFolderPopupWindow.dismiss();
                ImageSet imageSet = itemAdapter.get(position);
                if (null != imageSet) {
                    List<ImageItem> imageItems = imageSet.imageItems;
                    if (imagePicker.needShowCamera()) {
                        imageItems = new ArrayList<>();
                        imageItems.add(null);
                        imageItems.addAll(imageSet.imageItems);
                    }
                    setItems(imageItems);
                    imageSetBtn.setText(imageSet.name);
                }
                // scroll to the top
                getPullableViewContainer().getPullableView().smoothScrollToPosition(0);
            }, 100);
        });
        mFolderPopupWindow.setAdapter(itemAdapter = new ItemAdapter<ImageSet>(null) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageSet item = getItem(position);
                ViewHolder viewHolder = ViewHolder.getInstance(R.layout.list_item_folder, convertView, parent);
                ImageLoaderManager.getImageLoader().displayImage(viewHolder.id(R.id.cover).getImageView(), Uri.fromFile(new File(item.cover.path)).toString(), R.drawable.image_default);
                viewHolder.id(R.id.name).text(item.name);
                viewHolder.id(R.id.size).text(item.imageItems.size() + me().getResources().getString(R.string.ai_piece));
                viewHolder.id(R.id.indicator).visibility(selectedImageSetIndex == position ? View.VISIBLE : View.INVISIBLE);
                return viewHolder.getItemView();
            }
        });
    }

    // 设置屏幕透明度
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = me().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        me().getWindow().setAttributes(lp);
    }

    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;

    @Override
    public int getItemViewType(int position) {
        return imagePicker.needShowCamera() && position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return ItemViewHolder.newInstance(R.layout.grid_item_camera, viewGroup);
        } else {
            return ItemViewHolder.newInstance(R.layout.image_grid_item, viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position, ImageItem imageItem) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_CAMERA) {

        } else {
            boolean isSelected = imagePicker.isPicked(imageItem);
            holder.id(R.id.iv_thumb).selected(isSelected).image(Uri.fromFile(new File(imageItem.path)).toString(), R.drawable.image_default);
            if (imagePicker.isMultiMode()) {
                holder.id(R.id.iv_thumb_check).visibility(View.VISIBLE).tagWithCurrentId(position).checked(isSelected).checkedChange(this);
            } else {
                holder.id(R.id.iv_thumb_check).visibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(ItemViewHolder holder, int position, ImageItem item) {
        super.onItemClick(holder, position, item);

        if (getItemViewType(position) == ITEM_TYPE_CAMERA) {
            File file = Util.takePicture(me(), REQUEST_CODE_CAMERA);
            if (file != null) {
                takePictureImagePath = file.getAbsolutePath();
            }
        } else {
            //多选模式下，点击条目进行预览
            if (imagePicker.isMultiMode()) {
                ImagePreviewActivity.startForResult(me(), REQUEST_CODE_PREVIEW, selectedImageSetIndex, imagePicker.needShowCamera() ? --position : position);
            } else {
                //如果需要裁剪，就调用裁剪程序
                if (imagePicker.needCrop()) {
                    ImageCropActivity.startForResult(REQUEST_CODE_CROP, me(), item.path, imagePicker.cropWidth(), imagePicker.imageCropCompleteListener());
                } else {
                    imagePicker.clearPickedCache();
                    imagePicker.addInPickedCache(item);
                    imagePicker.notifyImagePickComplete();

                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Logger.i(TAG, "onCheckedChanged() isChecked = " + isChecked);
        int position = (int) buttonView.getTag(R.id.iv_thumb_check);
        ImageItem item = getItem(position);
        if (isChecked) {
            int pickedImageCount = imagePicker.pickedImageCount();
            if (pickedImageCount < imagePicker.maxSelectCount()) {
                imagePicker.addInPickedCache(item);
                updateStatus();
            } else {
                buttonView.setChecked(false);
                String text = getResources().getString(R.string.ai_you_have_a_select_limit, imagePicker.maxSelectCount());
                showToast(text);
            }
        } else {
            imagePicker.deleteFromPickedCache(item);
            updateStatus();
        }
    }

    private void updateStatus() {
        int pickedImageCount = imagePicker.pickedImageCount();
        if (pickedImageCount == 0) {
            previewBtn.setEnabled(false);
            previewBtn.setText("预览");

            completeBtn.setEnabled(false);
            completeBtn.setText("完成");
        } else {
            previewBtn.setEnabled(true);
            previewBtn.setText("预览(" + pickedImageCount + ")");

            completeBtn.setEnabled(true);
            completeBtn.setText("完成(" + pickedImageCount + "/" + imagePicker.maxSelectCount() + ")");
        }
    }
}
