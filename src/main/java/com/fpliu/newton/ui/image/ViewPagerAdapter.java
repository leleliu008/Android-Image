package com.fpliu.newton.ui.image;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager的Adapter
 *
 * @author 792793182@qq.com 2015-09-22.
 */
public abstract class ViewPagerAdapter<T> extends PagerAdapter {

    //数据项集合
    private List<T> items;

    /**
     * 构造方法
     *
     * @param items 要显示的项的数据列表
     */
    protected ViewPagerAdapter(List<T> items) {
        if (items == null) {
            this.items = new ArrayList<T>();
        } else {
            this.items = items;
        }
    }

    public void setItems(List<T> items) {
        if (items == null) {
            this.items = new ArrayList<T>();
        } else {
            this.items = items;
        }

        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        int count = items == null ? 0 : items.size();
        if (count > 1) {
            return Integer.MAX_VALUE;
        } else {
            return count;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int count = items == null ? 0 : items.size();
        if (count > 0) {
            position = position % count;
        }
        View convertView = getView(position, null, container);
        if (convertView != null) {
            container.addView(convertView, 0);
        }
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);

    private String getTag() {
        return getClass().getSimpleName();
    }
}
