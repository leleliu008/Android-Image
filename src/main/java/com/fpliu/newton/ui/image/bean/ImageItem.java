package com.fpliu.newton.ui.image.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageItem implements Parcelable {

    public String path;
    public String name;
    public long time;

    public ImageItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageItem imageItem = (ImageItem) o;

        if (time != imageItem.time) return false;
        return path != null ? path.equals(imageItem.path) : imageItem.path == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeLong(this.time);
    }

    protected ImageItem(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.time = in.readLong();
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
