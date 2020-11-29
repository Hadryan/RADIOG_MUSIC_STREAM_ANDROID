package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

public class NoticeModel implements Serializable {

    long id;
    String title;
    String title_bn;
    String body;
    String body_bn;
    String url_image;
    String url_redirection;
    String carrier;

    public NoticeModel() {
    }

    public NoticeModel(long id, String title, String title_bn, String body, String body_bn, String url_image, String url_redirection, String carrier) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.body = body;
        this.body_bn = body_bn;
        this.url_image = url_image;
        this.url_redirection = url_redirection;
        this.carrier = carrier;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody_bn() {
        return body_bn;
    }

    public void setBody_bn(String body_bn) {
        this.body_bn = body_bn;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }

    public String getUrl_redirection() {
        return url_redirection;
    }

    public void setUrl_redirection(String url_redirection) {
        this.url_redirection = url_redirection;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
}