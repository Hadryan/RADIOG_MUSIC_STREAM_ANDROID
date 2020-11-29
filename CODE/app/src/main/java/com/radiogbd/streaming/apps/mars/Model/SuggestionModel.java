package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by fojlesaikat on 12/29/17.
 */

public class SuggestionModel {

    int id;
    String title;
    String title_bn;
    String thumbnail;
    String highres;
    String release_date;

    public SuggestionModel() {
    }

    public SuggestionModel(int id, String title, String title_bn, String thumbnail, String highres, String release_date) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.thumbnail = thumbnail;
        this.highres = highres;
        this.release_date = release_date;
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

    public String getTitle_bn() {
        return title_bn;
    }

    public void setTitle_bn(String title_bn) {
        this.title_bn = title_bn;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getHighres() {
        return highres;
    }

    public void setHighres(String highres) {
        this.highres = highres;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
