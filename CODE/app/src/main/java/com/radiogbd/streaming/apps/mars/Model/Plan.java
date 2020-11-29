package com.radiogbd.streaming.apps.mars.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Plan {

    @SerializedName("idplan")
    @Expose
    private String idplan;
    @SerializedName("textplan")
    @Expose
    private String textplan;
    @SerializedName("textplan_bn")
    @Expose
    private String textplanBn;

    public String getIdplan() {
        return idplan;
    }

    public void setIdplan(String idplan) {
        this.idplan = idplan;
    }

    public String getTextplan() {
        return textplan;
    }

    public void setTextplan(String textplan) {
        this.textplan = textplan;
    }

    public String getTextplanBn() {
        return textplanBn;
    }

    public void setTextplanBn(String textplanBn) {
        this.textplanBn = textplanBn;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "idplan='" + idplan + '\'' +
                ", textplan='" + textplan + '\'' +
                ", textplanBn='" + textplanBn + '\'' +
                '}';
    }
}