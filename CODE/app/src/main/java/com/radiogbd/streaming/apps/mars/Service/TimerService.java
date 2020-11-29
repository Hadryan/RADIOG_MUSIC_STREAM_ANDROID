package com.radiogbd.streaming.apps.mars.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.radiogbd.streaming.apps.mars.Library.Utility;

/**
 * Created by Hp on 2/4/2018.
 */

public class TimerService extends Service {

    long sleepTime = 0;
    Utility utility;
    boolean timerRunning = false;
    private final IBinder timerBind = new TimerBinder();
    CustomCountDownTimers countDownTimers;
    long sleepSeconds;

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        utility = new Utility(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return timerBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void setSleepTime(long sleepTime){
        this.sleepTime = sleepTime;
        countDownTimers = new CustomCountDownTimers(this.sleepTime*60*1000,1000);
    }

    public void startTimer(){
        timerRunning = true;
        countDownTimers.start();
    }

    public void cancelTimer(){
        timerRunning = false;
        countDownTimers.cancel();
        sleepSeconds = 0;
    }

    public boolean isTimerRunning(){
        return timerRunning;
    }

    public void resetTime(){
        sleepTime = 0;
        timerRunning = false;
    }

    public long getRemainingSeconds(){
        return sleepSeconds;
    }

    private class CustomCountDownTimers extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CustomCountDownTimers(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            sleepSeconds = millisInFuture/1000;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sleepSeconds = sleepSeconds - 1;
        }

        @Override
        public void onFinish() {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                    .getInstance(TimerService.this);
            localBroadcastManager.sendBroadcast(new Intent(
                    "com.timer.close"));
        }
    }
}
