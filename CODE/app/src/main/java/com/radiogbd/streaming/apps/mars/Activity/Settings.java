package com.radiogbd.streaming.apps.mars.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.TimerService;

/**
 * Created by Hp on 1/4/2018.
 */

public class Settings extends AppCompatActivity {

    Utility utility;
    SwitchButton switchButton;
    ImageView backButton;
    TextView title, timer;
    EditText sleepTime;
    Button startButton, cancelButton;
    LinearLayout startState, stopState;
    boolean fromUser = false;

    //Sleep Timer Configuration
    private TimerService timerService;
    private Intent timeIntent;
    private boolean timerBound = false;
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility(this);
        utility.setRefreshState(true);
        setContentView(R.layout.layout_settings);
        switchButton = (SwitchButton)findViewById(R.id.languageSwitch);
        backButton = (ImageView) findViewById(R.id.backButton);
        sleepTime = (EditText) findViewById(R.id.sleepTime);
        startButton = (Button) findViewById(R.id.startBtn);
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        title = (TextView) findViewById(R.id.title);
        timer = (TextView) findViewById(R.id.timer);
        startState = (LinearLayout) findViewById(R.id.serviceStartState);
        stopState = (LinearLayout) findViewById(R.id.serviceStopState);
        final String language = utility.getLangauge();
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    utility.setLanguage("en");
                    //title.setText(getString(R.string.settings));
                }
                else{
                    utility.setLanguage("bn");
                    //title.setText(getString(R.string.settings_bn));
                }
//                if(fromUser) {
//                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
//                            .getInstance(Settings.this);
//                    localBroadcastManager.sendBroadcast(new Intent(
//                            "com.change.language"));
//                }
//                fromUser = true;
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sleepTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 120) {
                        utility.showToast("Max 120 Mins");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = sleepTime
                        .getText().toString();
                if(value.length()>0) {
                    int sleepValue = Integer.parseInt(value);
                    if(sleepValue<=120){
                        timerService.setSleepTime(sleepValue);
                        timerService.startTimer();
                        checkTimerStatus();
                        utility.clearText(new View[]{sleepTime});
                        utility.hideKeyboard(startButton);
                    }
                    else{
                        utility.showToast("Max 120 Mins");
                    }
                }
                else{
                    utility.showToast("Set Time");
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerService.cancelTimer();
                timerService.resetTime();
                checkTimerStatus();
            }
        });
        if(timeIntent == null){
            timeIntent = new Intent(Settings.this, TimerService.class);
            bindService(timeIntent, timerConnection, Context.BIND_AUTO_CREATE);
        }
        String languageKey = utility.getLangauge();
        if(languageKey.equals("bn")){
            switchButton.setChecked(false);
        }
        else{
            switchButton.setChecked(true);
        }
    }

    private void checkTimerStatus() {
        if(timerService.isTimerRunning()){
            startState.setVisibility(View.VISIBLE);
            stopState.setVisibility(View.GONE);
        }
        else{
            startState.setVisibility(View.GONE);
            stopState.setVisibility(View.VISIBLE);
        }
    }

    private ServiceConnection timerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            timerBound = true;
            checkTimerStatus();
            handler.postDelayed(runnable, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerBound = false;
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                timer.setText(utility.convertSecondsToHour(timerService.getRemainingSeconds()));
                handler.postDelayed(this, 1000);
            }
            catch (Exception ex){

            }
        }
    };
}
