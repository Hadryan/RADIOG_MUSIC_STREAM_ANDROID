package com.radiogbd.streaming.apps.mars.App;


import android.content.Context;
import com.radiogbd.streaming.apps.mars.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hp on 1/31/2018.
 */

public class AnalyticsTrackers {

    public enum Target{
        APP,
        // Add more trackers here if you need, and update the code in #get(Target) below
    }

    private static AnalyticsTrackers analyticsTracker;

    public static synchronized void initialize(Context context) {
        if (analyticsTracker != null) {
            throw new IllegalStateException("Extra call to initialize analytics trackers");
        }
    }

    public static synchronized AnalyticsTrackers getInstance() {
        if (analyticsTracker == null) {
            throw new IllegalStateException("Call initialize() before getInstance()");
        }

        return analyticsTracker;
    }

}
