package com.radiogbd.streaming.apps.mars.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.Html;

import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Notification.NotificationUtils;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 1/1/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    List<AlbumModel> albumModels;
    SubscriptionBox subscriptionBox;
    //PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        utility.logger("Ad Triggered");
        //checkForAlbum();
    }

    private void checkForAlbum(){
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");
//        wl.acquire();
        utility.writeToFile("Calling New Album API");
        Call<List<AlbumModel>> call = apiInterface.newAlbum(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                try{
                    utility.writeToFile("API Response Code "+response.code());
                    if(response.isSuccessful()&&response.code()==200){
                        //callNotification(100, new AlbumModel(1,"Aar Tomake","&#2438;&#2480; &#2468;&#2507;&#2478;&#2494;&#2453;&#2503;","/images/art-album/1.jpeg","2013-10-08","10 "));
                        albumModels = response.body();
                        utility.writeToFile("Album Size -> "+albumModels.size());
                        for(int i=0; i<albumModels.size(); i++){
                            utility.writeToFile("Album No "+(i+1)+" -> "+albumModels.get(i).getTitle());
                            AlbumModel albumModel = albumModels.get(i);
                            //callNotification(i, albumModel); Notification Has been Disabled
                        }
                    }
                    else{
                        utility.writeToFile("Response Code "+response.code());
                    }
                }
                catch (Exception ex){
                    utility.logger(ex.toString());
                    utility.writeToFile(ex.toString());
                }
                //wl.release();
            }

            @Override
            public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                utility.writeToFile(t.toString());
                //wl.release();
            }
        });
    }

    private void callNotification(int i, AlbumModel albumModel) {
        Intent resultIntent = new Intent(context, PlayList.class);
        resultIntent.putExtra("Album", albumModel);
        String title;
        if(utility.getLangauge().equals("bn")){
            title = String.valueOf(Html.fromHtml(albumModel.getTitle_bn()));
        }
        else{
            title = albumModel.getTitle();
        }
        resultIntent.putExtra("message", title);
        showNotificationMessageWithBigImage(i, context, title, utility.getLangauge().equals("bn")?context.getString(R.string.notificationBn):context.getString(R.string.notificationEn), resultIntent, context.getString(R.string.image_url)+albumModel.getThumbnail());
    }

    private void showNotificationMessageWithBigImage(int notificationId, Context context, String title, String message, Intent intent, String imageUrl) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = pm.isInteractive();
            }
            if (isScreenOn == false) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(10000);
            }
            NotificationUtils notificationUtils = new NotificationUtils(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(notificationId, title, message, intent, imageUrl);
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
    }


}
