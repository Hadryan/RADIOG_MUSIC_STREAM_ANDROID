package com.radiogbd.streaming.apps.mars.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.BaseApiClient;
import com.radiogbd.streaming.apps.mars.Http.BaseApiInterface;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.Log;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Notification.PlayerNotification;
import com.radiogbd.streaming.apps.mars.R;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/19/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private List<SongModel> songModels;
    private int songPosition = -1;
    private final IBinder musicBind = new MusicBinder();
    Utility utility;
    SubscriptionBox subscriptionBox;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    BaseApiInterface baseApiInterface = BaseApiClient.getBaseClient().create(BaseApiInterface.class);
    DB db;
    boolean isOffline;
    String keyword = "next";

    //Notification Part
    boolean showNotification = true;
    PlayerNotification playerNotification;
    boolean isRepeatOn = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        utility.logger("I am completed");
        logPlayerState(6);
        if (isRepeatOn) {
            songPosition--;
            if (songPosition < 0) {
                songPosition = songModels.size() - 1;
            }
        }
        nextSong();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        logPlayerState(1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mediaPlayer = new MediaPlayer();
        utility = new Utility(this);
        subscriptionBox = new SubscriptionBox(this);
        db = new DB(this);
        playerNotification = new PlayerNotification(this);
        initiateMediaPlayer();
    }

    private void initiateMediaPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    /*
     *  MEDIA PLAYER CONTROLLER
     * */

    public void setSongList(List<SongModel> songModels, boolean isOffline) {
        this.songModels = songModels;
        this.isOffline = isOffline;
    }

    public void setSongPosition(int songPosition) {
        this.songPosition = songPosition;
    }

    public boolean getOfflineStatus() {
        return isOffline;
    }

    public void selectSong() {
        try {
            String link = "";
            if (songModels.size() > 0) {
                if (songModels.get(songPosition).getStatus() != null) {
                    link = songModels.get(songPosition).getLink();
                    File file = new File(link);
                    if (!file.exists()) {
                        if (isAllSongDeleted()) {
                            playerReset();
                        } else {
                            utility.logger("Triggered from select song");
                            nextSong();
                        }
                    } else {
                        if (songModels.get(songPosition).getPremium().equals("Yes")) {
                            if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(songPosition).getId()))) {
                                mediaPlayer.setDataSource(link);
                                mediaPlayer.prepareAsync();
                                updateNotification();
                            } else if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(songPosition).getId())) == false) {
                                utility.logger("go to subs");
                                subscriptionBox.checkSubscription((String.valueOf(songModels.get(songPosition).getId())));
                            } else {
                                if (songModels.size() > 2) {
                                    if (keyword.equals("next")) {
                                        nextSong();
                                    } else {
                                        previousSong();
                                    }
                                }
                            }
                        } else {
                            mediaPlayer.setDataSource(link);
                            mediaPlayer.prepareAsync();
                            updateNotification();
                        }
                        /*if (songModels.get(songPosition).getPremium().equals("Yes")) {
                            switch (utility.getMdn()) {
                                case "18":
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("mdn", utility.getMsisdn());
                                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                                        checkSubscription(body, keyword, link);
                                    } catch (Exception ex) {
                                        utility.logger(ex.toString());
                                    }
                                    break;
                                case "19":
                                    checkSubscriptionBanglalink(link);
                                    break;
                                default:
                                    if (keyword.equals("next")) {
                                        nextSong();
                                    } else {
                                        previousSong();
                                    }
                                    break;
                            }
                        } else {
                            mediaPlayer.setDataSource(link);
                            mediaPlayer.prepareAsync();
                        }*/
                    }

                } else {
                    link = getString(R.string.image_url) + songModels.get(songPosition).getLink();
                    if (songModels.get(songPosition).getPremium().equals("Yes")) {
                        if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(songPosition).getId()))) {
                            mediaPlayer.setDataSource(link);
                            mediaPlayer.prepareAsync();
                            updateNotification();
                        } else if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(songPosition).getId())) == false) {
                            utility.logger("go to subs");
                            subscriptionBox.checkSubscription((String.valueOf(songModels.get(songPosition).getId())));
                        } else {
                            if (songModels.size() > 2) {
                                if (keyword.equals("next")) {
                                    nextSong();
                                } else {
                                    previousSong();
                                }
                            }
                        }
                            /*switch (utility.getMdn()) {
                                case "18":
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("mdn", utility.getMsisdn());
                                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                                        //checkSubscription(body, "next", link);
                                    } catch (Exception ex) {
                                        utility.logger(ex.toString());
                                    }
                                    break;
                                case "19":
                                    //checkSubscriptionBanglalink(link);
                                    break;
                                default:
                                    if(songModels.size()>2) {
                                        if (keyword.equals("next")) {
                                            nextSong();
                                        } else {
                                            previousSong();
                                        }
                                    }
                                    break;
                            }*/
                    } else {
                        mediaPlayer.setDataSource(link);
                        mediaPlayer.prepareAsync();
                        updateNotification();
                    }
                }

            } else {
                stopPlayer();
                playerReset();
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
    }

    private boolean isAllSongDeleted() {
        for (int i = 0; i < songModels.size() - 1; i++) {
            File file = new File(songModels.get(i).getLink());
            if (file.exists()) {
                return false;
            }
        }
        return true;
    }

    public void pauseSong() {
        mediaPlayer.pause();
        logPlayerState(2);
        clearNotification();
    }

    public void playSong() {
        mediaPlayer.start();
        logPlayerState(1);
        updateNotification();
    }

    public void nextSong() {
        //logPlayerState(6);
        songPosition++;
        if (songPosition >= songModels.size()) {
            songPosition = 0;
        }
        playerReset();
        keyword = "next";
        selectSong();
    }

    public void previousSong() {
        songPosition--;
        if (songPosition < 0) {
            songPosition = songModels.size() - 1;
        }
        keyword = "previous";
        selectSong();
    }

    public void setSeekPosition(int progress) {
        mediaPlayer.seekTo(progress);
    }

    public SongModel getCurrentSong() {
        if (songModels != null && songPosition != -1) {
            return songModels.get(songPosition);
        }
        return null;
    }

    public int getSongPosition() {
        return songPosition;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void playerReset() {
        mediaPlayer.reset();
    }

    public void stopPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public long getClipDuration() {
        return mediaPlayer.getDuration();
    }

    public long getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void updateNotification() {
        //Showing Notification
        if (showNotification) {
            playerNotification.setNotification(songModels.get(songPosition));
        }
    }

    public void clearNotification() {
        playerNotification.cancelNotification();
    }

    public void seekPlayer(int time) {
        mediaPlayer.seekTo(time);
    }

    public SongModel getNextSong() {
        if (songModels.size() > 0) {
            if ((songPosition + 1) >= songModels.size() - 1) {
                return songModels.get(0);
            } else {
                return songModels.get(songPosition + 1);
            }
        } else {
            stopPlayer();
            playerReset();
            return null;
        }
    }

    public int getSongPercentage() {
        return (int) (((double) getCurrentPosition() / getClipDuration()) * 100);
    }

    public void logPlayerState(int state) {
        if (songModels != null && songPosition != -1) {
            String logUrl = "log-" + songModels.get(songPosition).getId() + "-" + state + getSongPercentage();
            utility.logger("API calling: " + logUrl);
            Call<Log> call = apiInterface.logPlayer(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), logUrl);
            call.enqueue(new Callback<Log>() {
                @Override
                public void onResponse(Call<Log> call, Response<Log> response) {
                    try {

                        if (response.isSuccessful() && response.code() == 200) {
                            Log log = response.body();
                            utility.logger("Response body: " + log.getComment());
                        } else {
                            utility.logger("Response code: " + response.code());
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }

                @Override
                public void onFailure(Call<Log> call, Throwable t) {
                    utility.logger(t.toString());
                }
            });
        } else {
            utility.logger("Log can't insert due to Song not found");
        }
    }

    /*public void logPlayerState(int state, int percentage) {
        String logUrl = "log-" + songModels.get(songPosition).getId() + "-" + state + percentage;
        utility.logger("API calling: " + logUrl);
        Call<Log> call = apiInterface.logPlayer(getString(R.string.authorization_key), utility.getMsisdn(), utility.getFirebaseToken(), logUrl);
        call.enqueue(new Callback<Log>() {
            @Override
            public void onResponse(Call<Log> call, Response<Log> response) {
                try {

                    if (response.isSuccessful() && response.code() == 200) {
                        Log log = response.body();
                        utility.logger("Response body: " + log.getComment());
                    } else {
                        utility.logger("Response code: " + response.code());
                    }
                } catch (Exception ex) {
                    utility.logger(ex.toString());
                }
            }

            @Override
            public void onFailure(Call<Log> call, Throwable t) {
                utility.logger(t.toString());
            }
        });
    }*/

    public void setRepeatOn() {
        isRepeatOn = !isRepeatOn;
    }

    public boolean getRepeatOn() {
        return isRepeatOn;
    }
}
