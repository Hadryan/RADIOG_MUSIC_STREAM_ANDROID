package com.radiogbd.streaming.apps.mars.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Plantext {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("abbreviation")
    @Expose
    private String abbreviation;
    @SerializedName("carrier")
    @Expose
    private String carrier;
    @SerializedName("carrier_bn")
    @Expose
    private String carrierBn;
    @SerializedName("plans")
    @Expose
    private List<Plan> plans = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getCarrierBn() {
        return carrierBn;
    }

    public void setCarrierBn(String carrierBn) {
        this.carrierBn = carrierBn;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    @Override
    public String toString() {
        return "Plantext{" +
                "id='" + id + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", carrier='" + carrier + '\'' +
                ", carrierBn='" + carrierBn + '\'' +
                ", plans=" + plans +
                '}';
    }
}