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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fpliu.newton.log.Logger;
import com.fpliu.newton.ui.base.TextBtn;
import com.fpliu.newton.ui.base.UIUtil;
import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.Util;
import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.bean.ImageSet;
import com.fpliu.newton.ui.image.crop.CropActivity;
import com.fpliu.newton.ui.list.ItemAdapter;
import com.fpliu.newton.ui.list.PullableRecyclerViewActivity;
import com.fpliu.newton.ui.list.ViewHolder;
import com.fpliu.newton.ui.pullable.PullType;
import com.fpliu.newton.ui.pullable.PullableViewContainer;
import com.fpliu.newton.ui.recyclerview.ItemViewHolder;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesGridActivity extends PullableRecyclerViewActivity<ImageItem, ItemViewHolder>
        implements ImageSelectedChangeListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ImagesGridActivity";

    private static final String KEY_FILE_PATH = "imagePath";

    private static final int REQUEST_CODE_CAMERA = 1000;

    private static final int REQUEST_CODE_PREVIEW = 1001;

    private ImagePicker androidImagePicker;

    private Button completeBtn;

    private Button btnDir;

    private View mFooterView;

    private ListPopupWindow mFolderPopupWindow;

    private ItemAdapter<ImageSet> itemAdapter;

    private int selectedImageSetIndex;

    private String imagePath;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FILE_PATH, imagePath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidImagePicker = ImagePicker.getInstance();
        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString(KEY_FILE_PATH);
        }

        super.onCreate(savedInstanceState);

        setTitle("图片选择");
        getContentView().setHeadBackgroundColor(getResources().getColor(R.color.aip_main));
        getPullableViewContainer().getPullableView().setBackgroundColor(Color.BLACK);

        //多选模式下，右边有一个"完成"按钮
        if (androidImagePicker.isMultiMode()) {
            getContentView()
                    .setRightViewStrategy(new TextBtn() {
                        @Override
                        public Button onCreateView(RelativeLayout headView) {
                            completeBtn = super.onCreateView(headView);
                            completeBtn.setBackgroundResource(R.drawable.sel_top_ok);
                            return completeBtn;
                        }
                    })
                    .getRightBtnClickObservable()
                    .compose(bindToLifecycle())
                    .subscribe(o -> {
                        finish();
                        androidImagePicker.notifyOnImagePickComplete();
                    });
        }

        asGrid(4);
        clearItemDecorations();
        canPullDown(false);
        canPullUp(false);

        onRefreshOrLoadMore(null, null, 0, 0);
    }

    @Override
    public void onRefreshOrLoadMore(PullableViewContainer<RecyclerView> pullableViewContainer, PullType pullType, int pageNumber, int pageSize) {
        androidImagePicker.dataSource().loadData(me(), imageSetList -> {
            //说明没有图片
            if (imageSetList == null) {
                if (androidImagePicker.needShowCamera()) {
                    List<ImageItem> imageItems = new ArrayList<>();
                    imageItems.add(null);
                    setItems(imageItems);
                } else {
                    pullableViewContainer.finishRequest(pullType, true, "没有图片");
                }
            } else {
                androidImagePicker.setImageSets(imageSetList);

                ImageSet imageSet = imageSetList.get(0);
                List<ImageItem> imageItems = imageSet.imageItems;
                if (androidImagePicker.needShowCamera()) {
                    imageItems = new ArrayList<>();
                    imageItems.add(null);
                    imageItems.addAll(imageSet.imageItems);
                }
                setItems(imageItems);

                if (mFooterView == null) {
                    createFooter();
                }
                if (mFolderPopupWindow == null) {
                    createPopupFolderList(me());
                }
                btnDir.setText(imageSet.name);
                itemAdapter.setItems(imageSetList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        androidImagePicker.clearImageSets();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从预览界面返回
        if (requestCode == REQUEST_CODE_PREVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(RESULT_OK);
                finish();
                androidImagePicker.notifyOnImagePickComplete();
            }
        }
        //从拍照返回
        else if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (!TextUtils.isEmpty(imagePath)) {
                    Util.galleryAddPic(me(), imagePath);

                    finish();

                    //需要进行裁剪
                    if (androidImagePicker.needCrop()) {
                        Intent intent = new Intent();
                        intent.setClass(me(), CropActivity.class);
                        intent.putExtra(ImagePicker.KEY_PIC_PATH, imagePath);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    } else {
                        ImageItem item = new ImageItem(imagePath, "", -1);
                        androidImagePicker.clearSelectedImages();
                        androidImagePicker.addSelectedImageItem(-1, item);
                        androidImagePicker.notifyOnImagePickComplete();
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
        mFolderPopupWindow.setAnchorView(mFooterView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnDismissListener(() -> backgroundAlpha(1f));
        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        mFolderPopupWindow.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedImageSetIndex = position;
            new Handler().postDelayed(() -> {
                mFolderPopupWindow.dismiss();
                ImageSet imageSet = itemAdapter.get(position);
                if (null != imageSet) {
                    setItems(imageSet.imageItems);
                    btnDir.setText(imageSet.name);
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
                ImagePicker.getImageLoader().displayImage(viewHolder.id(R.id.cover).getImageView(), Uri.fromFile(new File(item.cover.path)).toString(), R.drawable.default_img);
                viewHolder.id(R.id.name).text(item.name);
                viewHolder.id(R.id.size).text(item.imageItems.size() + me().getResources().getString(R.string.piece));
                viewHolder.id(R.id.indicator).visibility(selectedImageSetIndex == position ? View.VISIBLE : View.INVISIBLE);
                return viewHolder.getItemView();
            }
        });
    }

    private void createFooter() {
        //添加Footer
        setViewAfterBody(R.layout.images_grid_footer);
        mFooterView = findViewById(R.id.footer_panel);
        btnDir = (Button) findViewById(R.id.footer_panel_dir_btn);
        RxView.clicks(btnDir).compose(bindToLifecycle()).subscribe(o -> {
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
        return androidImagePicker.needShowCamera() && position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        if (position == 0) {
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
            boolean isSelected = androidImagePicker.isSelect(position, imageItem);
            holder.id(R.id.iv_thumb).selected(isSelected).image(Uri.fromFile(new File(imageItem.path)).toString(), R.drawable.default_img);
            holder.id(R.id.iv_thumb_check).tagWithCurrentId(position).checked(isSelected).checkedChange(this).visibility(androidImagePicker.isMultiMode() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClick(ItemViewHolder holder, int position, ImageItem item) {
        Logger.i(TAG, "onItemClick() position = " + position);

        super.onItemClick(holder, position, item);

        if (getItemViewType(position) == ITEM_TYPE_CAMERA) {
            File file = Util.takePicture(me(), REQUEST_CODE_CAMERA);
            if (file != null) {
                imagePath = file.getAbsolutePath();
            }
        } else {
            //多选模式下，点击条目进行预览
            if (androidImagePicker.isMultiMode()) {
                ImagePreviewActivity.startForResult(me(), REQUEST_CODE_PREVIEW, selectedImageSetIndex, position);
            } else {
                //如果需要裁剪，就调用裁剪程序
                if (androidImagePicker.needCrop()) {
                    Intent intent = new Intent(ImagesGridActivity.this, CropActivity.class);
                    intent.putExtra(ImagePicker.KEY_PIC_PATH, getItem(position).path);
                    startActivity(intent);
                } else {
                    androidImagePicker.clearSelectedImages();
                    androidImagePicker.addSelectedImageItem(position, getItem(position));

                    setResult(RESULT_OK);
                    finish();

                    androidImagePicker.notifyOnImagePickComplete();
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
            if (androidImagePicker.getSelectImageCount() < androidImagePicker.maxSelectCount()) {
                androidImagePicker.addSelectedImageItem(position, item);
            } else {
                String text = getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.maxSelectCount());
                Toast.makeText(me(), text, Toast.LENGTH_SHORT).show();
            }
        } else {
            androidImagePicker.deleteSelectedImageItem(position, item);
        }
    }

    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (selectedItemsCount > 0) {
            completeBtn.setEnabled(true);
            completeBtn.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            completeBtn.setText(getResources().getString(R.string.complete));
            completeBtn.setEnabled(false);
        }
    }
}
