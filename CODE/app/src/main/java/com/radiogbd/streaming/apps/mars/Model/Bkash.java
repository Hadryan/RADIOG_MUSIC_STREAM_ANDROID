package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

public class Bkash implements Serializable {

    String msisdn;
    String status;
    long expiry;
    String url;

    public Bkash() {
    }

    public Bkash(String msisdn, String status, long expiry, String url) {
        this.msisdn = msisdn;
        this.status = status;
        this.expiry = expiry;
        this.url = url;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
