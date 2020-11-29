package com.radiogbd.streaming.apps.mars.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by Hp on 12/5/2017.
 */

public class GenreModel {

    int id;
    String title;
    String title_bn;
    String total;
    int banner;

    public GenreModel() {
    }

    public GenreModel(int id, String title, String title_bn, String total, int banner) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.total = total;
        this.banner = banner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getBanner() {
        return banner;
    }

    public void setBanner(int banner) {
        this.banner = banner;
    }

    public String getTitle_bn() {
        return title_bn;
    }

    public void setTitle_bn(String title_bn) {
        this.title_bn = title_bn;
    }
}


