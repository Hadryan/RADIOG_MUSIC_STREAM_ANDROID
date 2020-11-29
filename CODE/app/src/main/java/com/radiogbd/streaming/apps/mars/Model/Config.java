package com.radiogbd.streaming.apps.mars.Model;

public class Config {

    public static String LANGUAGE = "language";
    public static String MODE = "mode";
    public static String NOTICE = "notice";
    public static String NOTICE_MSG_BN = "notice_msg_bn";
    public static String NOTICE_MSG_EN = "notice_msg_en";
    public static String VERSION_CONTROL = "version_control";
    public static String VERSION_MSG_BN = "version_msg_bn";
    public static String VERSION_MSG_EN = "version_msg_en";
    public static String VERSION_UPDATE = "version_update";

    String default_language;
    String mode;
    String notice;
    String notice_msg_bn;
    String notice_msg_en;
    int version_control;
    String version_msg_bn;
    String version_msg_en;
    String version_update;

    public Config() {
    }

    public Config(String default_language, String mode, String notice, String notice_msg_bn, String notice_msg_en, int version_control, String version_msg_bn, String version_msg_en, String version_update) {
        this.default_language = default_language;
        this.mode = mode;
        this.notice = notice;
        this.notice_msg_bn = notice_msg_bn;
        this.notice_msg_en = notice_msg_en;
        this.version_control = version_control;
        this.version_msg_bn = version_msg_bn;
        this.version_msg_en = version_msg_en;
        this.version_update = version_update;
    }

    public String getDefault_language() {
        return default_language;
    }

    public void setDefault_language(String default_language) {
        this.default_language = default_language;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getNotice_msg_bn() {
        return notice_msg_bn;
    }

    public void setNotice_msg_bn(String notice_msg_bn) {
        this.notice_msg_bn = notice_msg_bn;
    }

    public String getNotice_msg_en() {
        return notice_msg_en;
    }

    public void setNotice_msg_en(String notice_msg_en) {
        this.notice_msg_en = notice_msg_en;
    }

    public int getVersion_control() {
        return version_control;
    }

    public void setVersion_control(int version_control) {
        this.version_control = version_control;
    }

    public String getVersion_msg_bn() {
        return version_msg_bn;
    }

    public void setVersion_msg_bn(String version_msg_bn) {
        this.version_msg_bn = version_msg_bn;
    }

    public String getVersion_msg_en() {
        return version_msg_en;
    }

    public void setVersion_msg_en(String version_msg_en) {
        this.version_msg_en = version_msg_en;
    }

    public String getVersion_update() {
        return version_update;
    }

    public void setVersion_update(String version_update) {
        this.version_update = version_update;
    }
}
