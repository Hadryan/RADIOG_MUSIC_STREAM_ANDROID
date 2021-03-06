package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

/**
 * Created by Hp on 12/12/2017.
 */

public class AlbumModel implements Serializable {

    int id;
    String title;
    String title_bn;
    String thumbnail;
    String release_date;
    String total;

    public AlbumModel() {
    }

    public AlbumModel(int id){
        this.id = id;
    }

    public AlbumModel(int id, String title, String title_bn, String thumbnail, String release_date, String total) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.thumbnail = thumbnail;
        this.release_date = release_date;
        this.total = total;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTitle_bn() {
        return title_bn;
    }

    public void setTitle_bn(String title_bn) {
        this.title_bn = title_bn;
    }
}
