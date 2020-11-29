package com.radiogbd.streaming.apps.mars.Model;

/**
 * Created by Hp on 9/11/2017.
 */

public class Banner {

    String browse;
    String browse_uri_inapp;
    String browse_uri_external;
    String uri_banner;

    public Banner() {
    }

    public Banner(String browse, String browse_uri_inapp, String browse_uri_external, String uri_banner) {
        this.browse = browse;
        this.browse_uri_inapp = browse_uri_inapp;
        this.browse_uri_external = browse_uri_external;
        this.uri_banner = uri_banner;
    }

    public String getBrowse() {
        return browse;
    }

    public void setBrowse(String browse) {
        this.browse = browse;
    }

    public String getBrowse_uri_inapp() {
        return browse_uri_inapp;
    }

    public void setBrowse_uri_inapp(String browse_uri_inapp) {
        this.browse_uri_inapp = browse_uri_inapp;
    }

    public String getBrowse_uri_external() {
        return browse_uri_external;
    }

    public void setBrowse_uri_external(String browse_uri_external) {
        this.browse_uri_external = browse_uri_external;
    }

    public String getUri_banner() {
        return uri_banner;
    }

    public void setUri_banner(String uri_banner) {
        this.uri_banner = uri_banner;
    }
}
