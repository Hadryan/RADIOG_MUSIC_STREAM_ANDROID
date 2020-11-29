package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

/**
 * Created by Hp on 6/26/2018.
 */

public class Master implements Serializable {

    String id;
    String msisdn;
    String expiry;
    String price;
    String comment;

    public Master() {
    }

    public Master(String id, String msisdn, String expiry, String price, String comment) {
        this.id = id;
        this.msisdn = msisdn;
        this.expiry = expiry;
        this.price = price;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
