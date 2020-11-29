package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by Hp on 12/12/2017.
 */

public class LikeModel {

    int id;
    String msisdn;
    String comment;

    public LikeModel() {
    }

    public LikeModel(int id, String msisdn, String comment) {
        this.id = id;
        this.msisdn = msisdn;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
