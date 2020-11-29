package com.radiogbd.streaming.apps.mars.Model;

public class Version {

    String version;
    String message;
    String message_bn;
    String action;

    public Version() {
    }

    public Version(String version, String message, String message_bn, String action) {
        this.version = version;
        this.message = message;
        this.message_bn = message_bn;
        this.action = action;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_bn() {
        return message_bn;
    }

    public void setMessage_bn(String message_bn) {
        this.message_bn = message_bn;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
