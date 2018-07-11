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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageSet imageSet = (ImageSet) o;

        if (name != null ? !name.equals(imageSet.name) : imageSet.name != null) return false;
        return path != null ? path.equals(imageSet.path) : imageSet.path == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
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
