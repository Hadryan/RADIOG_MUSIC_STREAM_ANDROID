package com.radiogbd.streaming.apps.mars.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobileModel{

    @SerializedName("mdn")
    @Expose
    private long mdn;

    public MobileModel(long mdn) {
        this.mdn = mdn;
    }

    public long getMdn() {
        return mdn;
    }

    public void setMdn(Integer mdn) {
        this.mdn = mdn;
    }

    @Override
    public String toString() {
        return "MobileModel{" +
                "mdn=" + mdn +
                '}';
    }
}