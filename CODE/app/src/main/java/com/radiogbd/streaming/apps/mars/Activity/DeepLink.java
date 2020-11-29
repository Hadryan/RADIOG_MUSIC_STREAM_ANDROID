package com.radiogbd.streaming.apps.mars.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Notification.NotificationUtils;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeepLink extends AppCompatActivity {

    Utility utility =  new Utility(this);
    private static final String LAUNCH_FROM_URL = "com.androidsrc.launchfrombrowser";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //utility.showToast("Deep Link");
        Intent intent = getIntent();
        if(intent != null && intent.getAction().equals(LAUNCH_FROM_URL)){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                String aid = bundle.getString("aid");
                String tid = bundle.getString("tid");
                Intent i = new Intent(this, Splash.class);
                i.putExtra("aid", aid);
                i.putExtra("tid", tid);
                startActivity(i);
                finish();
                //utility.showToast(aid+" - "+tid);
                //getSingleAlbum(aid, tid);
            }
            else{
                utility.showToast("Bundle is null");
            }
        }else{
            utility.showToast("Intent is null");
        }
    }
}
