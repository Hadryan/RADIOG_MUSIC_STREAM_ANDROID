package com.radiogbd.streaming.apps.mars.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.radiogbd.streaming.apps.mars.Adapter.SongAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.SuggestionAdapter;
import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.OnSwipeTouchListener;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.TriangleDrawable;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.LikeModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Model.SuggestionModel;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.DownloadService;
import com.radiogbd.streaming.apps.mars.Service.MusicService;
import com.warkiz.widget.IndicatorSeekBar;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/20/2017.
 */

public class Player extends AppCompatActivity implements PlayInterface {

    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);

    ImageView backButton;
    ImageView btnMore;
    CircleImageView profileImage;
    RelativeLayout radio_player;
    LinearLayout playerView;
    TextView songTitle;
    TextView songAlbum;
    TextView playTitle;
    ImageView likeButton;
    ImageView previousButton;
    ImageView playButton;
    ImageView nextButton;
    ImageView infoButton;
    ImageView repeatButton;
    TextView startTime;
    IndicatorSeekBar seekBar;
    TextView endTime;
    ImageView one, two, three, four, five, six, seven;
    Animation move_one, move_two, move_three, move_four, move_five, move_six, move_seven;
    List<SuggestionModel> suggestionModels;
    RecyclerView suggestionView;
    SuggestionAdapter suggestionAdapter;
    LinearLayout llShare, llOffline;
    TextView tvShare, tvOffline;


    //Service Configurations
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    Handler handler = new Handler();
    int songId = 0;
    DB db;

    int seekPosition;

    SongModel songModel;
    List<SongModel> songModels;
    Utility utility;
    String albumImage;
    //GestureDetector gestureDetector;

    private FirebaseAnalytics mFirebaseAnalytics;


    Handler suggestionHnadler = new Handler();
    SubscriptionBox subscriptionBox = new SubscriptionBox(this);

    LocalBroadcastManager broadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.timer.close")){
                finish();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_player);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        utility = new Utility(this);
        db = new DB(this);
        utility.setRefreshState(false);
        backButton = (ImageView) findViewById(R.id.backButton);
        btnMore = (ImageView) findViewById(R.id.btn_more);
        repeatButton = (ImageView) findViewById(R.id.repeatButton);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        radio_player = (RelativeLayout) findViewById(R.id.radio_player);
        playerView = (LinearLayout) findViewById(R.id.playerView);
        songTitle = (TextView) findViewById(R.id.songTitle);
        utility.setFont(songTitle);
        songTitle.setTextSize(16);
        songAlbum = (TextView) findViewById(R.id.songAlbum);
        utility.setFont(songAlbum);
        songAlbum.setTextSize(12);
        playTitle = (TextView) findViewById(R.id.playTitle);
        utility.setFont(playTitle);
        playTitle.setTextSize(16);
        likeButton = (ImageView) findViewById(R.id.likeButton);
        previousButton = (ImageView) findViewById(R.id.previousButton);
        playButton = (ImageView) findViewById(R.id.playButton);
        nextButton = (ImageView) findViewById(R.id.nextButton);
        infoButton = (ImageView) findViewById(R.id.infoButton);
        startTime = (TextView) findViewById(R.id.startTime);
        utility.setFont(startTime);
        startTime.setTextSize(12);
        seekBar = (IndicatorSeekBar) findViewById(R.id.musicSeekBar);
        endTime = (TextView) findViewById(R.id.endTime);
        utility.setFont(endTime);
        endTime.setTextSize(12);
        one = (ImageView) findViewById(R.id.music_icon_one);
        two = (ImageView) findViewById(R.id.music_icon_two);
        three = (ImageView) findViewById(R.id.music_icon_three);
        four = (ImageView) findViewById(R.id.music_icon_four);
        five = (ImageView) findViewById(R.id.music_icon_five);
        six = (ImageView) findViewById(R.id.music_icon_six);
        seven = (ImageView) findViewById(R.id.music_icon_seven);
        move_one = AnimationUtils.loadAnimation(this, R.anim.move_one);
        move_two = AnimationUtils.loadAnimation(this, R.anim.move_two);
        move_three = AnimationUtils.loadAnimation(this, R.anim.move_three);
        move_four = AnimationUtils.loadAnimation(this, R.anim.move_four);
        move_five = AnimationUtils.loadAnimation(this, R.anim.move_five);
        move_six = AnimationUtils.loadAnimation(this, R.anim.move_six);
        move_seven = AnimationUtils.loadAnimation(this, R.anim.move_seven);
        suggestionView = (RecyclerView) findViewById(R.id.suggestionView);
        LinearLayoutManager albumManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Player.this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                super.smoothScrollToPosition(recyclerView, state, position);
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(Player.this) {
                    private static final float SPEED = 4000f;

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        EasyPopup easyPopup = EasyPopup.create()
                .setContext(this)
                .setContentView(R.layout.popup_player_option)
                .setOnViewListener(new EasyPopup.OnViewListener() {
                    @Override
                    public void initViews(View view, EasyPopup easyPopup) {
                        View arrowView = view.findViewById(R.id.v_arrow);
                        arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.parseColor("#FFFFFF")));
                    }
                })
                .setFocusAndOutsideEnable(true)
                .apply();
        llShare = (LinearLayout)easyPopup.findViewById(R.id.ll_share);
        llOffline = (LinearLayout)easyPopup.findViewById(R.id.ll_offline);
        tvShare = (TextView)easyPopup.findViewById(R.id.tv_share);
        tvOffline = (TextView)easyPopup.findViewById(R.id.tv_offline);
        utility.setFonts(new View[]{tvShare, tvOffline});
        tvShare.setText(utility.getLangauge().equals("bn")?getString(R.string.share_bn):getString(R.string.share_en));
        tvOffline.setText(utility.getLangauge().equals("bn")?getString(R.string.download_bn):getString(R.string.download_en));
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easyPopup.showAtAnchorView(v, YGravity.BELOW, XGravity.RIGHT, 10, 0);
            }
        });
        llShare.setOnClickListener(v -> {
            easyPopup.dismiss();
            utility.shareTrack(
                    utility.getLangauge().equals("bn") ?
                            Html.fromHtml(songModel.getTitle_bn()) + " (" + Html.fromHtml(songModel.getArtist_bn()) + ")" :
                            songModel.getTitle() + " (" + songModel.getArtist_bn() + ")",
                    songModel.getId());
        });
        llOffline.setOnClickListener(view -> {
            easyPopup.dismiss();
            if(songModel!=null) {
                downloadSong(songModel);
            }
        });
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        suggestionView.setLayoutManager(layoutManager);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        int imageSize = (screenResolution.get(KeyWord.SCREEN_WIDTH) / 2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageSize);
        params.setMargins(0, 5, 0, 0);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (playIntent == null) {
            playIntent = new Intent(Player.this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
        playTitle.setText(utility.getLangauge().equals("bn") ?
                Html.fromHtml(getString(R.string.playing_bn)) :
                getString(R.string.playing_en)
        );
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songModel.getTokenFav().equals("No")) {
                    songModel.setTokenFav("Yes");
                    likeButton.setImageResource(R.drawable.ic_big_heart);
                } else {
                    songModel.setTokenFav("No");
                    likeButton.setImageResource(R.drawable.ic_heart);
                }
                setLikeStatus("mylist-" + songModel.getId());
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.getRepeatOn()){
                    musicService.setRepeatOn();
                }
                musicService.playerReset();
                musicService.previousSong();
                //musicService.selectSong();
                playButton.setImageResource(R.drawable.ic_pause);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    if (musicService.isPlaying()) {
                        musicService.pauseSong();
                        playButton.setImageResource(R.drawable.ic_play_button);
                        cancelAllAnimation();
                    } else {
                        musicService.playSong();
                        playButton.setImageResource(R.drawable.ic_pause);
                        startAnimation();
                    }
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.getRepeatOn()){
                    musicService.setRepeatOn();
                }
                musicService.playerReset();
                musicService.nextSong();
                //musicService.selectSong();
                playButton.setImageResource(R.drawable.ic_pause);
            }
        });
        seekBar.setClickable(false);
        seekBar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
                if (fromUserTouch) {
                    musicService.seekPlayer(progress);
                }
            }

            @Override
            public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
                musicService.logPlayerState(3);
                seekPosition = musicService.getSongPercentage();
                musicService.pauseSong();
                playButton.setImageResource(R.drawable.ic_play_button);
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                if(seekPosition<musicService.getSongPercentage()){
                    musicService.logPlayerState(4);
                }
                else{
                    musicService.logPlayerState(5);
                }
                musicService.playSong();
                playButton.setImageResource(R.drawable.ic_pause);
            }
        });
        radio_player.setOnTouchListener(new OnSwipeTouchListener(Player.this){
            @Override
            public void onSingleTap() {
                getSongList(String.valueOf(songId));
            }

            @Override
            public void onSwipeBottom() {
                finish();
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSongInfo();
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.setRepeatOn();
                if(musicService.getRepeatOn()){
                    utility.showToast("Repeat On");
                }
                else{
                    utility.showToast("Repeat Off");
                }
            }
        });
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.timer.close");
        broadcastManager.registerReceiver(broadcastReceiver, mIntentFilter);
    }

    @Override
    public void songPlaye(int position) {

    }

    @Override
    public void listPlay(List<SongModel> songModels, int position, boolean isOffline) {
        songModels = utility.filterSong(songModels);
        if(musicService.getRepeatOn()){
            musicService.setRepeatOn();
        }
        musicService.playerReset();
        musicService.setSongList(songModels, isOffline);
        musicService.setSongPosition(position);
        musicService.selectSong();
    }

    public void downloadSong(SongModel songModel) {
        utility.logger("Total Internal Memomry Size => "+utility.getTotalInternalMemorySize());
        utility.logger("Available Internal Memory size => "+utility.getAvailableInternalMemorySize());
        long fileSize = utility.checkFileSize(getString(R.string.image_url)+songModel.getLink());
        long availableSize = utility.getAvailableInternalMemorySize();
        if(fileSize<availableSize){
            try {
                db.open();
                if(utility.checkIfSongExists(songModel)){
                    utility.showToast("Already Downloaded");
                }
                else {
                    if (db.insertSong(songModel)) {
                        Intent intent = new Intent(Player.this, DownloadService.class);
                        intent.putExtra("song", songModel);
                        startService(intent);
                    } else {
                        utility.logger("Data not inserted");
                    }
                    db.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            utility.showToast("No Space");
        }
    }

    @Override
    public void lastSongDelete() {

    }

    private void showSongInfo() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.info_layout);
//        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
//        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
//        Button no = (Button) dialog.findViewById(R.id.dialog_no);
//        tv.setText(utility.getLangauge().equals("bn")?getString(R.string.exit_message):"Are you sure want to exit?");
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.info_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        ImageView songImage = (ImageView) dialog.findViewById(R.id.songImage);
        TextView songTitle = (TextView) dialog.findViewById(R.id.songTitle);
        TextView songAlbum = (TextView) dialog.findViewById(R.id.songAlbum);
        TextView songArtist = (TextView) dialog.findViewById(R.id.songArtist);
        TextView songLyrics = (TextView) dialog.findViewById(R.id.songLyrics);
        TextView songTune = (TextView) dialog.findViewById(R.id.songTune);
        TextView songNext = (TextView) dialog.findViewById(R.id.songNext);
        utility.setFont(songTitle);
        songTitle.setTextSize(16);
        utility.setFont(songAlbum);
        songAlbum.setTextSize(12);
        utility.setFont(songArtist);
        songArtist.setTextSize(12);
        utility.setFont(songLyrics);
        songLyrics.setTextSize(12);
        utility.setFont(songTune);
        songTune.setTextSize(12);
        utility.setFont(songNext);
        songNext.setTextSize(16);
        LinearLayout.LayoutParams imageParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mywidth);
        songImage.setLayoutParams(imageParam);
        Glide.with(Player.this)
                .load(getString(R.string.image_url) + songModel.getAlbumImage())
                .placeholder(R.drawable.app_icon)
                .error(R.drawable.app_icon)
                .into(songImage);
        songTitle.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.search_track) + ": " + Html.fromHtml(songModel.getTitle_bn())
                : "Track: " + songModel.getTitle());
        songAlbum.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.search_album) + ": " + Html.fromHtml(songModel.getAlbum_bn())
                : "Album: " + songModel.getAlbum());
        songArtist.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.search_artist) + ": " + Html.fromHtml(songModel.getArtist_bn())
                : "Artist: " + songModel.getArtist());
        songLyrics.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.search_lyrics) + ": " + Html.fromHtml(songModel.getLyrics_bn())
                : "Lyrics: " + songModel.getLyrics());
        songTune.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.search_tune) + ": " + Html.fromHtml(songModel.getTune_bn())
                : "Tune: " + songModel.getTune());
        songNext.setText(utility.getLangauge().equals("bn")
                ? getString(R.string.next_song) + ": " + Html.fromHtml(musicService.getNextSong().getTitle_bn())
                : "Next Song: " + musicService.getNextSong().getTitle());
        dialog.setCancelable(true);
        dialog.show();
    }

    private void showSongPlaylist(List<SongModel> songModels) {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_playlist);
        TextView tvAlbumTitle = (TextView)dialog.findViewById(R.id.tv_album_title);
        TextView tvSongCount = (TextView)dialog.findViewById(R.id.tv_song_count);
        ListView lvSong = (ListView)dialog.findViewById(R.id.listview);
        ImageView ivClose = (ImageView)dialog.findViewById(R.id.iv_close);
        utility.setFonts(new View[]{tvAlbumTitle, tvSongCount});
        tvAlbumTitle.setText(utility.getLangauge().equals("bn")?Html.fromHtml(songModel.getAlbum_bn()):songModel.getAlbum());
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if (songModels.size() > 0) {
            for (int i = 0; i < songModels.size(); i++) {
                songModels.get(i).setAlbumImage(albumImage);
            }
            SongAdapter songAdapter = new SongAdapter(Player.this, songModels, Player.this, KeyWord.DIALOG);
            songAdapter.notifyDataSetChanged();
            lvSong.setAdapter(songAdapter);
        }
        tvSongCount.setText(utility.getLangauge().equals("bn")?utility.convertToBangle(String.valueOf(songModels.size()-1))+" "+getString(R.string.song_bn): (songModels.size() - 1) +" "+getString(R.string.song_en));
        dialog.setCancelable(false);
        dialog.show();
    }


    private void setAnimAttributes(Animation animation, final ImageView imageView, long offset) {
        try {
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.INFINITE);
            animation.setStartOffset(offset);
            imageView.startAnimation(animation);
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }

    private void startAnimation() {
        setAnimAttributes(move_one, one, 1000);
        setAnimAttributes(move_two, two, 1000);
        setAnimAttributes(move_three, three, 2000);
        setAnimAttributes(move_four, four, 2000);
        setAnimAttributes(move_five, five, 1500);
        setAnimAttributes(move_six, six, 1500);
    }

    private void cancelAllAnimation() {
        try {
            one.clearAnimation();
            two.clearAnimation();
            three.clearAnimation();
            four.clearAnimation();
            five.clearAnimation();
            six.clearAnimation();
            seven.clearAnimation();
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            if(musicService!=null) {
                musicBound = true;
                handler.postDelayed(initPlayerStat, 0);
                if (musicService.isPlaying()) {
                    playButton.setImageResource(R.drawable.ic_pause);
                    startAnimation();
                } else {
                    playButton.setImageResource(R.drawable.ic_play_button);
                    cancelAllAnimation();
                }
                songModel = musicService.getCurrentSong();
                albumImage = songModel.getAlbumImage();
                if(songModel!=null) {
                    songTitle.setText(utility.getLangauge().equals("bn") ?
                            Html.fromHtml(songModel.getTitle_bn()) : songModel.getTitle());
                    songAlbum.setText(utility.getLangauge().equals("bn") ?
                            Html.fromHtml(songModel.getAlbum_bn()) : songModel.getAlbum());
                    if (songModel.getTokenFav().equals("Yes")) {
                        likeButton.setImageResource(R.drawable.ic_big_heart);
                    } else {
                        likeButton.setImageResource(R.drawable.ic_heart);
                    }
                    Glide.with(Player.this)
                            .load(getString(R.string.image_url) + songModel.getAlbumImage())
                            .asBitmap()
                            .placeholder(R.drawable.app_icon)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profileImage);
                    String endValue = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(musicService.getClipDuration()),
                            TimeUnit.MILLISECONDS.toSeconds(musicService.getClipDuration()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes(musicService.getClipDuration())));
                    endTime.setText(utility.getLangauge().equals("bn") ? utility.convertToBangle(endValue) : endValue);
                    seekBar.setMax(musicService.getClipDuration());
                    seekBar.setProgress(musicService.getCurrentPosition());
                    String startValue = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(musicService.getCurrentPosition()),
                            TimeUnit.MILLISECONDS.toSeconds(musicService.getCurrentPosition()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes(musicService.getCurrentPosition())));
                    startTime.setText(utility.getLangauge().equals("bn") ? utility.convertToBangle(startValue) : startValue);
                    getSuggestion();
                }
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
                    songId = musicService.getCurrentSong().getId();
                    if (musicService.isPlaying()) {
                        playButton.setImageResource(R.drawable.ic_pause);
                        if (songId != musicService.getCurrentSong().getId()) {
                            //songId = musicService.getCurrentSong().getId();
                            songModel = musicService.getCurrentSong();
                            albumImage = songModel.getAlbumImage();
                            songTitle.setText(utility.getLangauge().equals("bn") ?
                                    Html.fromHtml(songModel.getTitle_bn()) : songModel.getTitle());
                            songAlbum.setText(utility.getLangauge().equals("bn") ?
                                    Html.fromHtml(songModel.getAlbum_bn()) : songModel.getAlbum());
                            if (songModel.getTokenFav().equals("Yes")) {
                                likeButton.setImageResource(R.drawable.ic_big_heart);
                            } else {
                                likeButton.setImageResource(R.drawable.ic_heart);
                            }
                            Glide.with(Player.this)
                                    .load(getString(R.string.image_url) + songModel.getAlbumImage())
                                    .asBitmap()
                                    .placeholder(R.drawable.app_icon)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(profileImage);
                            String endValue = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(musicService.getClipDuration()),
                                    TimeUnit.MILLISECONDS.toSeconds(musicService.getClipDuration()) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                    toMinutes(musicService.getClipDuration())));
                            endTime.setText(utility.getLangauge().equals("bn") ? utility.convertToBangle(endValue) : endValue);
                            seekBar.setMax(musicService.getClipDuration());
                            getSuggestion();
                        }
                        String startValue = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(musicService.getCurrentPosition()),
                                TimeUnit.MILLISECONDS.toSeconds(musicService.getCurrentPosition()) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes(musicService.getCurrentPosition())));
                        startTime.setText(utility.getLangauge().equals("bn") ? utility.convertToBangle(startValue) : startValue);
                        seekBar.setProgress(musicService.getCurrentPosition());
                        if(musicService.getRepeatOn()){
                            repeatButton.setColorFilter(ContextCompat.getColor(Player.this,R.color.buttonColor));
                        }
                        else{
                            repeatButton.setColorFilter(ContextCompat.getColor(Player.this, R.color.ash));
                        }
                    }
                    else{
                        playButton.setImageResource(R.drawable.ic_play_button);
                    }
                }
                handler.postDelayed(this, 100);
            }
            catch (Exception ex){
                utility.logger(ex.toString());
                handler.removeCallbacks(initPlayerStat);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(musicConnection);
        handler.removeCallbacks(initPlayerStat);
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(musicService!=null&&musicService.isPlaying()) {
            musicService.updateNotification();
        }
    }

    private void setLikeStatus(String liked) {
        if(utility.isNetworkAvailable()) {
            utility.showProgress(true);
            Call<LikeModel> call = apiInterface.getLikeStatus(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), liked);
            call.enqueue(new Callback<LikeModel>() {
                @Override
                public void onResponse(Call<LikeModel> call, Response<LikeModel> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            LikeModel likeModel = response.body();
                            utility.showToast(likeModel.getComment());
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<LikeModel> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(getString(R.string.http_error));
                }
            });
        }
        else{
            utility.showToast(getString(R.string.no_internet));
        }
    }

    private void getSuggestion() {

        String searchKeyWord = "suggest-" + songModel.getId();
        Call<List<SuggestionModel>> call = apiInterface.getSuggestionList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), searchKeyWord);
        call.enqueue(new Callback<List<SuggestionModel>>() {
            @Override
            public void onResponse(Call<List<SuggestionModel>> call, Response<List<SuggestionModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        suggestionModels = response.body();
                        suggestionAdapter = new SuggestionAdapter(Player.this, suggestionModels);
                        suggestionView.setAdapter(suggestionAdapter);
//                        autoScroll();
//                        final Runnable runnable = new Runnable() {
//                            int count = 0;
//                            boolean flag = true;
//                            @Override
//                            public void run() {
//                                if(count < suggestionAdapter.getItemCount()){
//                                    if(count==suggestionAdapter.getItemCount()-1){
//                                        flag = false;
//                                    }else if(count == 0){
//                                        flag = true;
//                                    }
//                                    if(flag) count++;
//                                    else count--;
//
//                                    suggestionView.smoothScrollToPosition(count);
//                                    handler.postDelayed(this,speedScroll);
//                                }
//                            }
//                        };
//                        suggestionHnadler.postDelayed(runnable,speedScroll);
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                } else {
                    utility.showToast("Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SuggestionModel>> call, Throwable t) {
                utility.logger(t.toString());
                //utility.showToast(getString(R.string.http_error));
            }
        });
    }


    public void autoScroll() {
        final int speedScroll = 100;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
//                if(count == suggestionAdapter.getItemCount()+10)
//                    count = 0;
//                if(count < suggestionAdapter.getItemCount()+10){
//                    suggestionView.smoothScrollToPosition(++count);
//                    handler.postDelayed(this,speedScroll);
//                }
                if (count < suggestionAdapter.getItemCount()) {
                    if (count == suggestionAdapter.getItemCount() - 1) {
                        flag = false;
                    } else if (count == 0) {
                        flag = true;
                    }
                    if (flag) count++;
                    else count--;

                    suggestionView.smoothScrollToPosition(count);
                    handler.postDelayed(this, speedScroll);
                }
            }
        };
        handler.postDelayed(runnable, speedScroll);
    }

    private void getSongList(String trackId){
        if(utility.isNetworkAvailable()) {
            utility.showProgress(false);
            Call<List<SongModel>> call = apiInterface.getAlbumByTrack(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), trackId);
            call.enqueue(new Callback<List<SongModel>>() {
                @Override
                public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            songModels = response.body();
                            utility.logger("Song List Size = " + songModels.size());
                            showSongPlaylist(songModels);
                            /*if (songModels.size() > 0) {

                                songAdapter = new SongAdapter(Player.this, songModels, Player.this, true);
                                songAdapter.notifyDataSetChanged();
                                .setAdapter(adapter);
                                if (songModels.size() < 9) {
                                    listView.setVerticalScrollbarPosition(songModels.size() - 1);
                                }
                                if(getIntent().hasExtra("trackId")) {
                                    playSong(songModels);
                                }
                            }*/
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
        }
        else{
            utility.showToast(getString(R.string.no_internet));
        }
    }

}
