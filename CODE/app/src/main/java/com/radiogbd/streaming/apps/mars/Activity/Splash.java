package com.radiogbd.streaming.apps.mars.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.Config;
import com.radiogbd.streaming.apps.mars.Model.Msisdn;
import com.radiogbd.streaming.apps.mars.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Splash extends AppCompatActivity {

    Utility utility = new Utility(this);
    private FirebaseAnalytics mFirebaseAnalytics;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    SubscriptionBox subscriptionBox = new SubscriptionBox(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("ABIR", "FCM token: " + refreshedToken);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        utility.setFullScreen();
        setContentView(R.layout.splash_layout);
        utility.getNetworkInfo();
        HashMap<String, String> map = utility.getDeviceInfo();
        gotoMain(6000);
        /*try {
            if(utility.getSubscriptionStatus()<System.currentTimeMillis()) {
                getMsisdn();
            }
            else{
                gotoMain(6000);
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }*/

    }

    private void getConfig(){
        //utility.showProgress(true);
        Call<List<Config>> call = apiInterface.getConfig(utility.getAuthorization(), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<Config>>() {
            @Override
            public void onResponse(Call<List<Config>> call, Response<List<Config>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<Config> configs = response.body();
                    Config config = configs.get(0);
                    utility.setConfig(config);
                    if(utility.isFirstTime()) {
                        if (utility.getConfig().getDefault_language().equals("BN")) {
                            utility.setLanguage("bn");
                        } else {
                            utility.setLanguage("en");
                        }
                        utility.setFirstTime();
                    }
                }
                callActivity();
            }

            @Override
            public void onFailure(Call<List<Config>> call, Throwable t) {
                callActivity();
            }
        });
    }

    private void gotoMain(long waitTime) {
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(waitTime);
                    getConfig();

                } catch (Exception e) {

                }
            }
        };

        background.start();
    }

    private void callActivity() {
        Intent i=new Intent(getBaseContext(),Main.class);
        if(getIntent().hasExtra("aid")&&getIntent().hasExtra("tid")){
            i.putExtra("aid",getIntent().getExtras().getString("aid"));
            i.putExtra("tid",getIntent().getExtras().getString("tid"));
        }
        startActivity(i);
        finish();
    }

    /*private void getMsisdn(){
        Call<Msisdn> call = apiInterface.getMdn(getString(R.string.authorization_key), String.valueOf(utility.getMsisdn()), utility.getFirebaseToken());
        call.enqueue(new Callback<Msisdn>() {
            @Override
            public void onResponse(Call<Msisdn> call, Response<Msisdn> response) {
                if(response.isSuccessful()&&response.code()==200){
                    Msisdn msisdn = response.body();
                    utility.writeMsisdn(msisdn);
                    utility.logger(String.valueOf(msisdn.getMsisdn()));
                }
                gotoMain(6000);
            }

            @Override
            public void onFailure(Call<Msisdn> call, Throwable t) {
                gotoMain(6000);
            }
        });
    }*/


}
