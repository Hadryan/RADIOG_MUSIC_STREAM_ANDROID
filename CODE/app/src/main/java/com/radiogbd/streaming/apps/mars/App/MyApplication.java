package com.radiogbd.streaming.apps.mars.App;

import android.app.Application;

/**
 * Developed by Fojle Rabbi Saikat on 11/19/2016.
 * Owned by Bitmakers Ltd.
 * Contact fojle.rabbi@bitmakers-bd.com
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

}
