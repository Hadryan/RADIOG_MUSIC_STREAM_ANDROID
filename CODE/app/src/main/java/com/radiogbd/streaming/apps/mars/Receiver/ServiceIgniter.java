package com.radiogbd.streaming.apps.mars.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.R;

/**
 * Created by Hp on 1/2/2018.
 */

public class ServiceIgniter extends BroadcastReceiver {

    Context context;
    Utility utility;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utility = new Utility(this.context);
        utility.writeToFile("Boot Completed");
        String[] schedules = this.context.getResources().getStringArray(R.array.schedules);
        for (int i = 0; i < schedules.length; i++) {
            String[] values = schedules[i].split(":");
            utility.setAlarm(Integer.parseInt(values[0]), Integer.parseInt(values[1]), 4878+i);
        }
//        utility.writeTotalDataUsage(utility.getDataUsage("send"),utility.getDataUsage("received"));
    }
}
