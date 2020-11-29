package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by Hp on 1/1/2018.
 */

public class Log {

    int id;
    long msisdn;
    String comment;

    public Log() {
    }

    public Log(int id, long msisdn, String comment) {
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

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
