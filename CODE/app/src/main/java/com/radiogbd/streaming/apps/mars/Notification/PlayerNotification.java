package com.radiogbd.streaming.apps.mars.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.widget.RemoteViews;

import com.radiogbd.streaming.apps.mars.Activity.Main;
import com.radiogbd.streaming.apps.mars.Activity.Notification;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hp on 5/27/2018.
 */

public class PlayerNotification {

    public static int NOTIFICATION_ID = 10107;
    String channelId = "RADIOG_ID";
    String channelName = "RADIOG_NAME";
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    Context context;
    Utility utility;
    Bitmap bitmap = null;
    RemoteViews remoteViews;
    SongModel currentSongModel = new SongModel(0);
    NotificationManager notificationManager;
    NotificationCompat.Builder notification;

    public PlayerNotification(Context context) {
        this.context = context;
        utility = new Utility(this.context);
    }

    public void setNotification(SongModel songModel) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, Notification.class);
        /*Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName())
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        new DownloadImage().execute(context.getString(R.string.image_url) + songModel.getAlbumImage());
        remoteViews.setTextViewText(
                R.id.songTitle,
                utility.getLangauge().equals("bn")
                        ? context.getString(R.string.playing_bn) + " " + Html.fromHtml(songModel.getTitle_bn()) + " " + context.getString(R.string.featuring_bn) + " " + Html.fromHtml(songModel.getArtist_bn())
                        : context.getString(R.string.playing_en) + " " + songModel.getTitle() + " " + context.getString(R.string.featuring_en) + " " + songModel.getArtist()
        );
        remoteViews.setTextViewText(
                R.id.songAlbum,
                utility.getLangauge().equals("bn")
                        ? context.getString(R.string.album_bn) + ": " + Html.fromHtml(songModel.getAlbum_bn())
                        : context.getString(R.string.album_en) + ": " + songModel.getAlbum()
        );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(null, null);
            notificationManager.createNotificationChannel(mChannel);
        }
        notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_play_noti)
                .setContentTitle(
                        utility.getLangauge().equals("bn")
                                ? context.getString(R.string.playing_bn) + Html.fromHtml(songModel.getTitle_bn())
                                : context.getString(R.string.playing_en) + songModel.getTitle()
                )
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .setOngoing(true)
                .setAutoCancel(false);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    public void cancelNotification(){
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public static Intent newLauncherIntent(final Context context) {
        final Intent intent = new Intent(context, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return intent;
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

}
