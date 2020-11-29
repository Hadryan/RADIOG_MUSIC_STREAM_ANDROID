package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

/**
 * Created by Hp on 4/17/2018.
 */

public class Msisdn implements Serializable {

    int id;
    String operator;
    long msisdn;
    String comment;

    public Msisdn() {
    }

    public Msisdn(int id, long msisdn, String comment) {
        this.id = id;
        this.msisdn = msisdn;
        this.comment = comment;
    }

    public Msisdn(int id, String operator, long msisdn, String comment) {
        this.id = id;
        this.operator = operator;
        this.msisdn = msisdn;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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
