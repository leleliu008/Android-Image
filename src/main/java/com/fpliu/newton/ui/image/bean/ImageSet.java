package com.fpliu.newton.ui.image.bean;

import java.io.Serializable;
import java.util.List;

public class ImageSet implements Serializable {
    public String name;
    public String path;
    public ImageItem cover;
    public List<ImageItem> imageItems;

    @Override
    public boolean equals(Object o) {
        try {
            ImageSet other = (ImageSet) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "ImageSet{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", cover=" + cover +
                ", imageItems=" + imageItems +
                '}';
    }
}
