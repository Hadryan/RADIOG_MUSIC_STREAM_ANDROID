package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by Hp on 12/5/2017.
 */

public class ExclusiveModel {

    int id;
    String txt_album;
    String txt_album_bn;
    String path;
    String release_date;

    public ExclusiveModel() {
    }

    public ExclusiveModel(int id, String txt_album, String txt_album_bn, String path, String release_date) {
        this.id = id;
        this.txt_album = txt_album;
        this.txt_album_bn = txt_album_bn;
        this.path = path;
        this.release_date = release_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxt_album() {
        return txt_album;
    }

    public void setTxt_album(String txt_album) {
        this.txt_album = txt_album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTxt_album_bn() {
        return txt_album_bn;
    }

    public void setTxt_album_bn(String txt_album_bn) {
        this.txt_album_bn = txt_album_bn;
    }
}
