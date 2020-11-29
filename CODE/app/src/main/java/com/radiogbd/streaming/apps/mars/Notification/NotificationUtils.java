package com.radiogbd.streaming.apps.mars.Notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RemoteViews;

import com.radiogbd.streaming.apps.mars.Activity.Main;
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Developed by Fojle Rabbi Saikat on 1/30/2017.
 * Owned by Bitmakers Ltd.
 * Contact fojle.rabbi@bitmakers-bd.com
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;
    public int notificationId;
    String channelId = "rg_firebase_id";
    String channelName = "rg_firebase_name";
    int importance = NotificationManager.IMPORTANCE_HIGH;
    RemoteViews remoteViews;
    NotificationManager notificationManager;
    NotificationCompat.Builder notification;
    public static int NOTIFICATION_ID = 10110;
    Bitmap bitmap = null;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    SubscriptionBox subscriptionBox;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        utility = new Utility(this.mContext);
        subscriptionBox = new SubscriptionBox(this.mContext);
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        showNotificationMessage(0, title, message, intent, null);
    }

    public void showNotificationMessage(int notificationId, final String title, final String message, Intent intent, String imageUrl) {
        // Check for empty push message

        this.notificationId = notificationId;
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        //final int icon = R.drawable.rg;

        //Intent backIntent = new Intent(mContext, Main.class);
        //backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivities(
                        mContext,
                        0,
                        new Intent[]{intent},
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext, channelId);

//        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                + "://" + mContext.getPackageName() + "/raw/notification");

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(notificationId, bitmap, mBuilder, title, message, resultPendingIntent, alarmSound);
                    //playNotificationSound();
                } else {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent, alarmSound);
                    //playNotificationSound();
                }
            }
        } else {
            showSmallNotification(mBuilder, title, message, resultPendingIntent, alarmSound);
            //playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder
                .setSmallIcon(R.drawable.rg)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setVibrate(new long[]{1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setStyle(inboxStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.rg))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(Config.NOTIFICATION_ID, notification);
    }

    private void showBigNotification(int notificationId, Bitmap bitmap, NotificationCompat.Builder mBuilder, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder
                .setSmallIcon(R.drawable.rg)
                .setTicker(title)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.rg))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(notificationId, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public void setNotification(String title, String body, String albumId, String trackId, String imagePath) {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.firebase_notification);
        remoteViews.setTextViewText(R.id.songTitle, title);
        remoteViews.setTextViewText(R.id.songAlbum, body);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notification = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(R.drawable.rg)
                .setContentTitle(title)
                .setCustomContentView(remoteViews)
                .setAutoCancel(true);
        getSingleAlbum(albumId, trackId, imagePath);
        //notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public class DownloadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImage(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(bitmap);
            bitmap = b;
            remoteViews.setImageViewBitmap(R.id.songImage, bitmap);
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
    }

    public Bitmap downloadImage(String imageUrl) {
        InputStream in;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(in);
            return myBitmap;
        } catch (Exception ex) {
            return null;
        }
    }

    private void getSingleAlbum(String albumId, String trackId, String imagePath){
        Call<List<AlbumModel>> call = apiInterface.getSingleAlbum(mContext.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(),"single_album-"+albumId);
        call.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                if(response.isSuccessful()&&response.code()==200){
                    List<AlbumModel> albumModels = response.body();
                    if(albumModels.size()>0) {
                        new NotificationUtils.DownloadImage().execute(imagePath);
                        Intent intent = null;
                        if(isAppIsInBackground(mContext)){
                            intent = new Intent(mContext, Main.class);
                            intent.putExtra("Album", albumModels.get(0));
                            intent.putExtra("trackId", trackId);
                        }
                        else {
                            intent = new Intent(mContext, PlayList.class);
                            intent.putExtra("Album", albumModels.get(0));
                            intent.putExtra("trackId", trackId);
                        }
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);
                        notificationManager.notify(NOTIFICATION_ID, notification.build());
                    }
                    else{
                        defaultNotification(imagePath);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                utility.logger(t.toString());
                defaultNotification(imagePath);
            }
        });
    }

    private void defaultNotification(String imagePath) {
        new DownloadImage().execute(imagePath);
        Intent intent = new Intent(mContext, Main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

}
