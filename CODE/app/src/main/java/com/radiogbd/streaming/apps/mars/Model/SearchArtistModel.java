package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by Hp on 12/12/2017.
 */

public class SearchArtistModel {

    int id;
    String title;
    String title_bn;
    String albumImage;

    public SearchArtistModel() {
    }

    public SearchArtistModel(int id, String title, String title_bn, String albumImage) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.albumImage = albumImage;
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

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getTitle_bn() {
        return title_bn;
    }

    public void setTitle_bn(String title_bn) {
        this.title_bn = title_bn;
    }
}
