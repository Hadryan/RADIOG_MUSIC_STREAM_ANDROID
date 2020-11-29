package com.radiogbd.streaming.apps.mars.Service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 1/10/2018.
 */

public class DownloadService extends IntentService {

    Utility utility;
    boolean isDownloading = false;
    DB db;
    SongModel songModel;
    String channelId = "RADIOG_ID";
    String channelName = "RADIOG_NAME";
    int importance = NotificationManager.IMPORTANCE_HIGH;


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        utility = new Utility(this);
        db = new DB(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        songModel = (SongModel) intent.getExtras().getSerializable("song");
        startDownload();
    }

    public void startDownload() {
        new Downloader().execute();
    }

    public class Downloader extends AsyncTask<SongModel, String, String>{

        @Override
        protected String doInBackground(SongModel... songModels) {
            isDownloading = true;
            OutputStream fileOutputStream = null;
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            File directory = null;
            String filePath = "";
            try {
                directory = new File(getFilesDir(), "RadioG");
                filePath = directory.getAbsolutePath() + "/" + songModel.getId() + ".mp3";
                fileOutputStream = new FileOutputStream(filePath);
                inputStream = null;
                int count;
                URL url = new URL(getString(R.string.image_url) + songModel.getLink());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    utility.logger("Connection Interrupted");
                }
                long fileLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                byte[] data = new byte[4096];
                int total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, count);
                }
                isDownloading = false;
                db.open();
                db.updateLocalFilePath(songModel.getId(), filePath);
                db.updateSongStatus(songModel.getId(), KeyWord.DOWNLOAD_FINISHED);
                db.close();
            } catch (Exception ex) {
                utility.logger(ex.toString());
                isDownloading = false;
                db.open();
                    db.updateLocalFilePath(songModel.getId(), filePath);
                    db.updateSongStatus(songModel.getId(), KeyWord.DOWNLOAD_CORRUPTED);
                db.close();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    connection.disconnect();
                } catch (Exception ex) {
                    utility.logger(ex.toString());
                }
                isDownloading = false;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utility.logger("Download "+songModel.getId()+" Started");
            showNotification(songModel, "Download in Progress", true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            utility.logger("Download "+songModel.getId()+" Finished");
            showNotification(songModel, "Download Finished", false);
        }
    }

    private void showNotification(SongModel songModel, String message, boolean isFinished){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(songModel.getTitle())
                .setContentText(message)
                .setSmallIcon(R.drawable.rg);
        builder.setProgress(0, 0, isFinished);
        if(!isFinished) {
            builder.setSound(Uri.parse("android.resource://"
                    + getPackageName() + "/" + R.raw.notification));
            builder.setAutoCancel(true);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(songModel.getId(),builder.build());
    }

}
