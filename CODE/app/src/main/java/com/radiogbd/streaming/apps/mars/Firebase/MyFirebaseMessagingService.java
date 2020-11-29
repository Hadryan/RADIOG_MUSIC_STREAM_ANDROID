package com.radiogbd.streaming.apps.mars.Firebase;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.Html;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.radiogbd.streaming.apps.mars.Activity.Main;
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Http.BaseApiClient;
import com.radiogbd.streaming.apps.mars.Http.BaseApiInterface;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Notification.NotificationUtils;
import com.radiogbd.streaming.apps.mars.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 2/27/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    Utility utility = new Utility(this);
    int FIREBASE_NOTIFICATION = 48788784;
    NotificationUtils notificationUtils = new NotificationUtils(this);
    ContentApiInterface apiInterface = BaseApiClient.getBaseClient().create(ContentApiInterface.class);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //utility.logger("From: " + remoteMessage.getFrom());
        //utility.logger("Notification Message Body: " + remoteMessage.getNotification().getBody());
        Map<String, String> map = remoteMessage.getData();
        Intent resultIntent = new Intent(this, Main.class);
//        AlbumModel albumModel = new AlbumModel();
//        albumModel.setId(897);
//        albumModel.setTitle("Shohor Chere");
//        albumModel.setTitle_bn("&#2486;&#2489;&#2480; &#2459;&#2503;&#2524;&#2503;");
//        albumModel.setThumbnail("/images/art-album/897.jpeg");
//        albumModel.setRelease_date("2018-02-22");
//        albumModel.setTotal("1");
        resultIntent.putExtra("albumId", map.get("id_album"));
        resultIntent.putExtra("trackId", map.get("id_track"));
        resultIntent.setAction("com.radiogbd.streaming.apps.firebase");
        sendBroadcast(resultIntent);
        notificationUtils.setNotification(
                map.get("name"),
                map.get("desc"),
                map.get("id_album"),
                map.get("id_track"),
                map.get("icon_path")
                );
        //showNotificationMessageWithBigImage(FIREBASE_NOTIFICATION, this, utility.getLangauge().equals("bn")?getString(R.string.notificationBn):getString(R.string.notificationEn), remoteMessage.getNotification().getBody(), resultIntent, getString(R.string.image_url)+albumModel.getThumbnail());
        //showNotificationMessageWithBigImage(FIREBASE_NOTIFICATION, this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), resultIntent, "");
    }

    /*private void checkForAlbum(int id){
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");
//        wl.acquire();
        Call<List<SongModel>> call = apiInterface.getAlbum(getString(R.string.authorization_key), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken(),"get_album-"+id);
        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                try{
                    utility.writeToFile("API Response Code "+response.code());
                    if(response.isSuccessful()&&response.code()==200){
                        //callNotification(100, new AlbumModel(1,"Aar Tomake","&#2438;&#2480; &#2468;&#2507;&#2478;&#2494;&#2453;&#2503;","/images/art-album/1.jpeg","2013-10-08","10 "));
                        List<SongModel> songModels = new ArrayList<>();
                        songModels = response.body();
                        utility.writeToFile("Album Size -> "+songModels.size());
                        if(songModels.size()>0) {
                            SongModel songModel = songModels.get(0);
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
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                utility.writeToFile(t.toString());
                //wl.release();
            }
        });
    }*/

    private void callNotification(int i, AlbumModel albumModel) {
        Intent resultIntent = new Intent(this, PlayList.class);
        resultIntent.putExtra("Album", albumModel);
        String title;
        if(utility.getLangauge().equals("bn")){
            title = String.valueOf(Html.fromHtml(albumModel.getTitle_bn()));
        }
        else{
            title = albumModel.getTitle();
        }
        resultIntent.putExtra("message", title);
        showNotificationMessageWithBigImage(i, this, title, utility.getLangauge().equals("bn")?getString(R.string.notificationBn):getString(R.string.notificationEn), resultIntent, getString(R.string.image_url)+albumModel.getThumbnail());
    }

    private void showNotificationMessageWithBigImage(int notificationId, Context context, String title, String message, Intent intent, String imageUrl) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = pm.isInteractive();
            }
            else {
                isScreenOn = pm.isScreenOn();
            }
            if (isScreenOn == false) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
                wl_cpu.acquire(10000);
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
