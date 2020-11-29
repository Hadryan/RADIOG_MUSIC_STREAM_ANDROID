package com.radiogbd.streaming.apps.mars.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nineoldandroids.view.ViewHelper;
import com.radiogbd.streaming.apps.mars.Adapter.SongAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.DownloadService;
import com.radiogbd.streaming.apps.mars.Service.MusicService;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/14/2017.
 */

public class PlayList extends AppCompatActivity implements ObservableScrollViewCallbacks, PlayInterface {

    AlbumModel albumModel;
    ImageView albumImage;
    GifImageView loadingImage;
    ImageView backButton;
    TextView albumTitle;
    TextView albumArtist;
    TextView titleAlbum;
    ObservableListView listView;
    RelativeLayout albumCover;
    RelativeLayout image;
    View titleLabel;
    List<SongModel> songModels;
    Utility utility;
    SongAdapter adapter;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    int bannerWidth;
    int bannerHeight;
    HashMap<String, Integer> screenResolution;

    //Service Configurations
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    Handler handler = new Handler();
    int songId = 0;

    //Download Service COnfiguration
    private DownloadService downloadService;
    private Intent downloadIntent;

    //Mini Player Configuration
    LinearLayout miniPlayer;
    ImageView playerStat;
    TextView songTitle;
    //ProgressBar progressBar;

    String trackId;

    private FirebaseAnalytics mFirebaseAnalytics;
    SubscriptionBox subscriptionBox = new SubscriptionBox(this);

    LocalBroadcastManager broadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.timer.close")) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_playlist);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        utility = new Utility(this);
        utility.setRefreshState(false);
        albumModel = (AlbumModel) getIntent().getSerializableExtra("Album");
        albumImage = (ImageView) findViewById(R.id.albumImage);
        image = (RelativeLayout) findViewById(R.id.image);
        loadingImage = (GifImageView) findViewById(R.id.loadingImage);
        backButton = (ImageView) findViewById(R.id.backButton);
        albumTitle = (TextView) findViewById(R.id.albumTitle);
        albumArtist = (TextView) findViewById(R.id.albumArtist);
        listView = (ObservableListView) findViewById(R.id.songList);
        albumCover = (RelativeLayout) findViewById(R.id.albumCover);
        titleAlbum = (TextView) findViewById(R.id.titleAlbum);
        titleLabel = (View) findViewById(R.id.titleLabel);
        miniPlayer = (LinearLayout) findViewById(R.id.miniPalyer);
        playerStat = (ImageView) findViewById(R.id.playerStat);
        songTitle = (TextView) findViewById(R.id.songTitle);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        utility.setFont(albumTitle);
        albumTitle.setTextSize(20);
        utility.setFont(albumArtist);
        albumArtist.setTextSize(16);
        utility.setFont(titleAlbum);
        titleAlbum.setTextSize(20);
        utility.setFont(songTitle);
        songTitle.setTextSize(16);
//        Picasso.with(this)
//                .load("http://radiogbd.com"+ albumModel.getThumbnail())
//                .error(R.drawable.app_icon)
//                .placeholder(R.drawable.app_icon)
//                .into(albumImage);
        Glide.with(this)
                .load(getString(R.string.image_url) + albumModel.getThumbnail())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(albumImage) {
                    @Override
                    public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        loadingImage.setVisibility(View.GONE);
                    }
                });
        albumTitle.setText(utility.getLangauge().equals("bn")
                ? Html.fromHtml(albumModel.getTitle_bn())
                : albumModel.getTitle());
//        albumArtist.setText(utility.getLangauge().equals("bn")
//                ? getString(R.string.release_date_bn)+": "+utility.convertToBangle(albumModel.getRelease_date())
//                : getString(R.string.release_date_en)+": "+albumModel.getRelease_date());
        albumArtist.setText("");
        titleAlbum.setText(utility.getLangauge().equals("bn")
                ? Html.fromHtml(albumModel.getTitle_bn())
                : albumModel.getTitle());
        screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = (bannerWidth / 2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        albumCover.setLayoutParams(params);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView.setScrollViewCallbacks(this);
        playerStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    if (musicService.isPlaying()) {
                        musicService.pauseSong();
                        songTitle.setSelected(false);
                    } else {
                        musicService.playSong();
                        songTitle.setSelected(true);
                    }
                }
            }
        });
        if (playIntent == null) {
            playIntent = new Intent(PlayList.this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        miniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayList.this, Player.class);
                startActivity(intent);
            }
        });
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.timer.close");
        broadcastManager.registerReceiver(broadcastReceiver, mIntentFilter);
        getSongList(String.valueOf(albumModel.getId()));
    }

    private void getSongList(String albumId) {
        if (utility.isNetworkAvailable()) {
            utility.showProgress(false);
            Call<List<SongModel>> call = apiInterface.getSongListByAlbum(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), albumId);
            call.enqueue(new Callback<List<SongModel>>() {
                @Override
                public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            songModels = response.body();
                            utility.logger("Song List Size = " + songModels.size());
                            if (songModels.size() > 0) {
                                for (int i = 0; i < songModels.size(); i++) {
                                    songModels.get(i).setAlbumImage(albumModel.getThumbnail());
                                }
                                adapter = new SongAdapter(PlayList.this, songModels, PlayList.this, KeyWord.ACTIVITY);
                                adapter.notifyDataSetChanged();
                                listView.setAdapter(adapter);
                                if (songModels.size() < 9) {
                                    listView.setVerticalScrollbarPosition(songModels.size() - 1);
                                }
                                if (getIntent().hasExtra("trackId")) {
                                    playSong(songModels);
                                }
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<SongModel>> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(getString(R.string.no_internet));
        }
    }

    private void playSong(List<SongModel> songModels) {
        trackId = getIntent().getExtras().get("trackId").toString();
        if (!trackId.equals("0")) {
            int position = utility.getSongPosition(songModels, Integer.parseInt(trackId));
            if (position != -1) {
                listView.setSelection(position);
                listPlay(songModels, position, false);
            }
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                musicBound = true;
                if (musicService.isPlaying()) {
                    setMiniPlayerInfo();
                    handler.postDelayed(initPlayerStat, 0);
                    songTitle.setSelected(true);
                }
            } catch (Exception ex) {
                utility.logger(ex.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }

    };


    Runnable initPlayerStat = new Runnable() {
        @Override
        public void run() {
            try {
                if (musicService != null) {
                    if (musicService.isPlaying()) {
                        if (songId != musicService.getCurrentSong().getId()) {
                            setMiniPlayerInfo();
                            songId = musicService.getCurrentSong().getId();
                        }
                        //progressBar.setProgress((int) musicService.getCurrentPosition());
                        miniPlayer.setVisibility(View.VISIBLE);
                        playerStat.setImageResource(R.drawable.ic_pause);
                    } else {
                        playerStat.setImageResource(R.drawable.ic_play_button);
                    }
                }
                handler.postDelayed(this, 100);
            } catch (Exception ex) {
                utility.logger(ex.toString());
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null && musicService.isPlaying()) {
            musicService.updateNotification();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //handler.removeCallbacks(initPlayerStat);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
        handler.removeCallbacks(initPlayerStat);
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        scrollY = scrollY / 2;
        utility.logger("Banner Height: " + bannerHeight + ", ScrollY: " + scrollY);
        ViewHelper.setTranslationY(albumCover, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(listView, Math.max(0, -scrollY + bannerHeight));
        if (scrollY < (bannerHeight / 2)) {
            ViewHelper.setAlpha(titleLabel, 0);
            ViewHelper.setAlpha(titleAlbum, 0);
        } else if (scrollY >= (bannerHeight / 2) && scrollY <= (bannerHeight / 3) * 2) {
            int alphaValue = ((bannerHeight / 3) * 2) - (bannerHeight / 2);
            int scaleValue = ((bannerHeight / 3) * 2) - scrollY;
            String value = String.format("%.1f", 1.0 - (float) scaleValue / alphaValue);
            double alpha = Double.parseDouble(value);
            ViewHelper.setAlpha(titleLabel, (float) alpha);
            ViewHelper.setAlpha(titleAlbum, 0);
        } else if (scrollY > (bannerHeight / 3) * 2) {
            ViewHelper.setAlpha(titleLabel, 1);
            ViewHelper.setAlpha(titleAlbum, 1);
        }
    }

    /*
    * @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        utility.logger("Banner Height: "+bannerHeight+", ScrollY: "+String.valueOf(scrollY));
        ViewHelper.setTranslationY(albumCover, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(listView, Math.max(0, -scrollY+bannerHeight));
        if (scrollY < (bannerHeight / 2)) {
            ViewHelper.setAlpha(titleLabel, 0);
            ViewHelper.setAlpha(titleAlbum, 0);
        } else if (scrollY >= (bannerHeight / 2) && scrollY <= (bannerHeight / 3) * 2) {
            int alphaValue = ((bannerHeight / 3) * 2) - (bannerHeight / 2);
            int scaleValue = ((bannerHeight / 3) * 2) - scrollY;
            String value = String.format("%.1f", 1.0 - (float) scaleValue / alphaValue);
            double alpha = Double.parseDouble(value);
            ViewHelper.setAlpha(titleLabel, (float) alpha);
            ViewHelper.setAlpha(titleAlbum, 0);
        } else if (scrollY > (bannerHeight / 3) * 2) {
            ViewHelper.setAlpha(titleLabel, 1);
            ViewHelper.setAlpha(titleAlbum, 1);
        }
    }
    * */

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void songPlaye(int position) {

    }

    @Override
    public void listPlay(List<SongModel> songModels, int position, boolean isOffline) {
        //do nothing
        songModels = utility.filterSong(songModels);
        if (musicService.getRepeatOn()) {
            musicService.setRepeatOn();
        }
        musicService.playerReset();
        musicService.setSongList(songModels, isOffline);
        musicService.setSongPosition(position);
        musicService.selectSong();
        setMiniPlayerInfo();
        songTitle.setSelected(true);
        handler.removeCallbacks(initPlayerStat);
        handler.postDelayed(initPlayerStat, 0);
    }

    @Override
    public void downloadSong(SongModel songModel) {
        //downloadService.addSong(songModel);
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("song", songModel);
        startService(intent);
        //downloadService.startDownload();
    }

    @Override
    public void lastSongDelete() {
        //do nothing
    }

    private void setMiniPlayerInfo() {
        SongModel songModel = musicService.getCurrentSong();
        if (musicService.isPlaying()) {
            //progressBar.setMax((int) musicService.getClipDuration());
        } else {
            //progressBar.setMax(100);
        }
        songTitle.setText(
                utility.getLangauge().equals("bn") ?
                        Html.fromHtml(songModel.getTitle_bn() + " " + getString(R.string.featuring_bn) + " " + songModel.getArtist_bn()) :
                        songModel.getTitle() + " " + getString(R.string.featuring_en) + " " + songModel.getArtist()
        );
    }
}
