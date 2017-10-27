package com.fpliu.newton.ui.image.picker;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fpliu.newton.ui.image.R;
import com.fpliu.newton.ui.image.bean.ImageItem;
import com.fpliu.newton.ui.image.preview.PreviewManager;
import com.fpliu.newton.ui.recyclerview.ItemAdapter;
import com.fpliu.newton.ui.recyclerview.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片选择展示面板
 *
 * @author 792793182@qq.com 2017-10-25.
 */
public class ImagePickLayout extends RecyclerView {

    /**
     * 展示几列，默认是3列
     */
    private int spanCount = 3;

    /**
     * 是否显示拍照的按钮
     */
    private boolean needShowCamera = true;

    /**
     * 选择完成或者拍照完成后，是否需要进行裁剪
     */
    private boolean needCrop = false;

    /**
     * 裁剪宽度
     */
    private int cropWidth = 240;

    /**
     * 裁剪高度
     */
    private int cropHeight = 240;

    /**
     * 选择模式：单选或者多选
     */
    private int selectMode = ImagePicker.SELECT_MODE_MULTI;

    /**
     * 多选模式下，最多可以选择的图片数量，默认最多选择9张
     */
    private int maxSelectCount = 9;

    private int addThumbnailRes = R.drawable.ic_add_pic;

    private int deleteThumbnailRes = R.drawable.ic_delete_image;

    private int defaultThumbnailRes = R.drawable.default_img;

    private ImageView.ScaleType thumbnailScaleType = ImageView.ScaleType.CENTER_CROP;

    private IconShape thumbnailShape = IconShape.ORIGIN;

    public enum IconShape {
        ORIGIN,
        CIRCLE,
        ROUND_RECT
    }

    private int radius = 8;

    private ItemAdapter<ImageItem, ItemViewHolder> itemAdapter;

    private OnImagesChangedListener onImagesChangedListener;

    public ImagePickLayout(Context context) {
        super(context);
        init(context);
    }

    public ImagePickLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        imageItems.add(null);
        setLayoutManager(new GridLayoutManager(context, spanCount));
        setAdapter(itemAdapter = new ItemAdapter<ImageItem, ItemViewHolder>(imageItems) {

            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
                return ItemViewHolder.newInstance(R.layout.image_selected_layout, viewGroup);
            }

            @Override
            public void onBindViewHolder(ItemViewHolder holder, int position, ImageItem imageItem) {
                holder.id(R.id.image_selected_layout_delete).tagWithCurrentId(position).clicked(this).image(deleteThumbnailRes);
                holder.id(R.id.image_selected_layout_image).tagWithCurrentId(position).clicked(this).scaleType(thumbnailScaleType);
                if (imageItem == null) {
                    holder.image(addThumbnailRes);
                } else {
                    switch (thumbnailShape) {
                        case ORIGIN:
                            holder.image(imageItem.path, defaultThumbnailRes);
                            break;
                        case CIRCLE:
                            holder.imageCircle(imageItem.path, defaultThumbnailRes);
                            break;
                        case ROUND_RECT:
                            holder.imageRound(imageItem.path, defaultThumbnailRes, radius);
                            break;
                    }
                }
            }

            @Override
            public void onClick(View view) {
                super.onClick(view);
                int id = view.getId();
                if (id == R.id.image_selected_layout_image) {
                    int position = (Integer) view.getTag(R.id.image_selected_layout_image);
                    ImageItem imageItem = getItem(position);
                    //如果是添加图片按钮
                    if (imageItem == null) {
                        ImagePicker.getInstance().needShowCamera(needShowCamera).needCrop(needCrop).cropWidth(cropWidth).cropHeight(cropHeight).selectMode(selectMode).maxSelectCount(maxSelectCount).onImagePickComplete(items -> {
                            removeLastItem();
                            for (ImageItem image : items) {
                                if (size() < maxSelectCount) {
                                    add(image);
                                } else {
                                    break;
                                }
                            }
                            if (size() < maxSelectCount) {
                                add(null);
                            }
                            if (onImagesChangedListener != null) {
                                onImagesChangedListener.onImagesChanged(getItems());
                            }
                        }).pick((Activity) context);
                    } else {
                        ArrayList<ImageItem> items = (ArrayList<ImageItem>) getItems();
                        items = (ArrayList<ImageItem>) items.clone();
                        items.remove(items.size() - 1);
                        PreviewManager.startImageItemPreview((Activity) context, position, items);
                    }
                } else if (id == R.id.image_selected_layout_delete) {
                    int position = (Integer) view.getTag(R.id.image_selected_layout_delete);
                    if (getLastItem() == null) {
                        remove(position);
                    } else {
                        remove(position);
                        add(null);
                    }
                    if (onImagesChangedListener != null) {
                        onImagesChangedListener.onImagesChanged(getItems());
                    }
                }
            }
        });
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public void setNeedShowCamera(boolean needShowCamera) {
        this.needShowCamera = needShowCamera;
    }

    public void setNeedCrop(boolean needCrop) {
        this.needCrop = needCrop;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    public List<ImageItem> getItems() {
        return itemAdapter.getItems();
    }

    public void setAddThumbnailRes(int addThumbnailRes) {
        this.addThumbnailRes = addThumbnailRes;
    }

    public void setDeleteThumbnailRes(int deleteThumbnailRes) {
        this.deleteThumbnailRes = deleteThumbnailRes;
    }

    public void setDefaultThumbnailRes(int defaultThumbnailRes) {
        this.defaultThumbnailRes = defaultThumbnailRes;
    }

    public void setThumbnailScaleType(ImageView.ScaleType thumbnailScaleType) {
        this.thumbnailScaleType = thumbnailScaleType;
    }

    public void setThumbnailShape(IconShape thumbnailShape) {
        this.thumbnailShape = thumbnailShape;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setOnImagesChangedListener(OnImagesChangedListener onImagesChangedListener) {
        this.onImagesChangedListener = onImagesChangedListener;
    }

    public interface OnImagesChangedListener {
        void onImagesChanged(List<ImageItem> currentImageItems);
    }
}
