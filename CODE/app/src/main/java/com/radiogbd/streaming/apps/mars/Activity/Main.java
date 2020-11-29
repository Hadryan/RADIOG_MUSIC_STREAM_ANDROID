package com.radiogbd.streaming.apps.mars.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Fragment.Category;
import com.radiogbd.streaming.apps.mars.Fragment.Exclusive;
import com.radiogbd.streaming.apps.mars.Fragment.Free;
import com.radiogbd.streaming.apps.mars.Fragment.Hits;
import com.radiogbd.streaming.apps.mars.Fragment.Home;
import com.radiogbd.streaming.apps.mars.Fragment.Language;
import com.radiogbd.streaming.apps.mars.Fragment.MyList;
import com.radiogbd.streaming.apps.mars.Fragment.NewAlbum;
import com.radiogbd.streaming.apps.mars.Fragment.Notice;
import com.radiogbd.streaming.apps.mars.Fragment.Offline;
import com.radiogbd.streaming.apps.mars.Fragment.Premium;
import com.radiogbd.streaming.apps.mars.Fragment.Search;
import com.radiogbd.streaming.apps.mars.Fragment.Selected;
import com.radiogbd.streaming.apps.mars.Fragment.Subscription;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.LanguageInterface;
import com.radiogbd.streaming.apps.mars.Interface.NoticeInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.Config;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Model.Version;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.MusicService;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/10/2017.
 */

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LanguageInterface, PlayInterface, NoticeInterface {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Utility utility = new Utility(this);
    TextView pageTitle;
    LinearLayout titleLayout, searchLayout;
    ImageView searchButton;
    EditText searchKeyword;
    SubscriptionBox subscriptionBox = new SubscriptionBox(this);
//    Rect droidTarget, verticalScrollTarget;
//    Drawable droid, verticalScroll;
//    Banner banner;

    //Service Configurations
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    Handler handler = new Handler();
    int songId = 0;

    //Mini Player Configuration
    LinearLayout miniPlayer;
    ImageView playerStat;
    TextView songTitle;
    //ProgressBar progressBar;

    //Sleep Timer Configuration
    //private TimerService timerService;
    //private Intent timeIntent;
    private boolean timerBound = false;

    public boolean doRefresh = false;
    LocalBroadcastManager broadcastManager;
    BroadcastReceiver languageReceiver;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.main_layout);
            if (getIntent().hasExtra("exit")) {
                if (getIntent().getExtras().get("exit").equals("yes")) {
                    finish();
                    System.exit(0);
                }
            }
            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            pageTitle = (TextView) findViewById(R.id.page_title);
            miniPlayer = (LinearLayout) findViewById(R.id.miniPalyer);
            playerStat = (ImageView) findViewById(R.id.playerStat);
            songTitle = (TextView) findViewById(R.id.songTitle);
            //progressBar = (ProgressBar) findViewById(R.id.progressBar);
            titleLayout = (LinearLayout) findViewById(R.id.titleLayout);
            searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
            searchButton = (ImageView) findViewById(R.id.searchButton);
            searchKeyword = (EditText) findViewById(R.id.searchKeyword);
            searchKeyword.setHint(utility.getLangauge().equals("bn") ? getString(R.string.search) : "Search");
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            navigationView.setNavigationItemSelectedListener(this);
            utility.setFont(pageTitle);
            utility.setFont(songTitle);
            utility.setFont(searchKeyword);
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
                playIntent = new Intent(Main.this, MusicService.class);
                bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                startService(playIntent);
            }
//            if (timeIntent == null) {
//                timeIntent = new Intent(Main.this, TimerService.class);
//                //bindService(timeIntent, timerConnection, Context.BIND_AUTO_CREATE);
//                startService(timeIntent);
//            }
            miniPlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Main.this, Player.class);
                    startActivity(intent);
                }
            });
            Menu menu = navigationView.getMenu();
            utility.setMenuFont(menu);
            /*if(!utility.operatorExisted()) {
                menu.findItem(R.id.nav_subscription).setVisible(false);
            }*/
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawers();
                    }
                    if (titleLayout.getVisibility() == View.VISIBLE) {
                        titleLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
                        searchKeyword.requestFocus();
                        utility.showKeyboard();
                    } else {
                        navigateSearch(searchKeyword.getText().toString());
                    }
                }
            });
            searchKeyword.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            navigateSearch(searchKeyword.getText().toString());
                        }
                    }
                    return false;
                }
            });
            String[] schedules = getResources().getStringArray(R.array.schedules);
            for (int i = 0; i < schedules.length; i++) {
                String[] values = schedules[i].split(":");
                utility.setAlarm(Integer.parseInt(values[0]), Integer.parseInt(values[1]), 4878 + i);
            }
            broadcastManager = LocalBroadcastManager.getInstance(this);
            IntentFilter sleepFilter = new IntentFilter();
            sleepFilter.addAction("com.timer.close");
            changeLanguage();
            String topTitle = getResources().getString(R.string.titleEn);
            if (utility.getLangauge().equals("bn")) {
                topTitle = getResources().getString(R.string.titleBn);
            }
            pageTitle.setText(topTitle);
            callFragment(R.id.nav_home, new Home(Main.this, this), KeyWord.HOME, topTitle);
            changeNavigationText();
            if (musicService != null && musicService.isPlaying()) {
                setMiniPlayerInfo();
                handler.postDelayed(initPlayerStat, 0);
                songTitle.setSelected(true);
            }
            //getAppVersion();
            if(utility.checkVersion(utility.getConfig().getVersion_control())){
                showVersionDialog();
            }
            else{
                navigateApp();
            }
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }


    private void navigateSearch(String keyword) {
        try {
            utility.hideKeyboard(searchButton);
            titleLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
            if (keyword.length() > 0) {
                List<android.support.v4.app.Fragment> fragments = fragmentManager.getFragments();
                android.support.v4.app.Fragment fragment = fragments.get(fragments.size() - 1);
                if (fragment != null && fragment.getTag().equals("search")) {
                    fragmentManager.popBackStackImmediate();
                }

                //utility.showToast("Searching "+searchKeyword.getText().toString());
                Search search = new Search(Main.this, keyword, Main.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, search, "search");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            utility.clearText(new View[]{searchKeyword});
        }
        catch (Exception ex){
            //need api call
            utility.logger(ex.toString());
            utility.showToast("Search not working");
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        utility.logger("Main Activity Paused");
        //unbindService(musicConnection);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            // Handle navigation view item clicks here.
            drawerLayout.closeDrawers();
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                String topTitle = getResources().getString(R.string.titleEn);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.titleBn);
                }
                callFragment(R.id.nav_home, new Home(Main.this, this), KeyWord.HOME, topTitle);
            } else if (id == R.id.nav_premium) {
                String topTitle = getResources().getString(R.string.premium);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.premium_bn);
                }
                callFragment(R.id.nav_premium, new Premium(Main.this), KeyWord.PREMIUM, topTitle);
            }else if (id == R.id.nav_free) {
                String topTitle = getResources().getString(R.string.free);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.free_bn);
                }
                callFragment(R.id.nav_free, new Free(Main.this), KeyWord.FREE, topTitle);
            }else if (id == R.id.nav_hits) {
                String topTitle = getResources().getString(R.string.hits);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.hit_bn);
                }
                callFragment(R.id.nav_hits, new Hits(Main.this, Main.this), KeyWord.HITS, topTitle);
            } else if (id == R.id.nav_selected) {
                String topTitle = getResources().getString(R.string.selected);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.selected_bn);
                }
                callFragment(R.id.nav_selected, new Selected(Main.this, Main.this), KeyWord.SELECTED, topTitle);
            } else if (id == R.id.nav_mylist) {
                String topTitle = getResources().getString(R.string.mylist);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.mylist_bn);
                }
                callFragment(R.id.nav_mylist, new MyList(Main.this, Main.this), KeyWord.MY_LIST, topTitle);
            } else if (id == R.id.nav_exclusive) {
                String topTitle = getResources().getString(R.string.exclusive);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.exclusive_bn);
                }
                callFragment(R.id.nav_exclusive, new Exclusive(Main.this), KeyWord.EXCLUSIVE, topTitle);
            } else if (id == R.id.nav_newalbum) {
                String topTitle = getResources().getString(R.string.new_album);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.new_album_bn);
                }
                callFragment(R.id.nav_newalbum, new NewAlbum(Main.this), KeyWord.NEW_ALBUM, topTitle);
            } else if (id == R.id.nav_category) {
                String topTitle = getResources().getString(R.string.catagory);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.catagory_bn);
                }
                callFragment(R.id.nav_category, new Category(Main.this), KeyWord.CATEGORY, topTitle);
            } else if (id == R.id.nav_offline) {
                String topTitle = getResources().getString(R.string.offline);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.offline_bn);
                }
                callFragment(R.id.nav_offline, new Offline(Main.this, Main.this), KeyWord.OFFLINE, topTitle);
            }else if (id == R.id.nav_notice) {
                String topTitle = getResources().getString(R.string.notice);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.notice_bn);
                }
                callFragment(R.id.nav_notice, new Notice(Main.this), KeyWord.NOTICE, topTitle);
            } else if (id == R.id.nav_language) {
                //utility.showToast("Call Language");
                callFragment(R.id.nav_language, new Language(Main.this, this), KeyWord.LANGUAGE, "Language");
            }
            else if (id == R.id.nav_subscription) {
                String topTitle = getResources().getString(R.string.subscription);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.subscription_bn);
                }
                callFragment(R.id.nav_subscription, new Subscription(Main.this), KeyWord.SUBSCRIPTION, topTitle);
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception ex) {
            utility.call_error(ex);
            return false;
        }
    }

    private void callFragment(int itemId, android.support.v4.app.Fragment fragment, String tag, String title) {
        pageTitle.setText(title);
        //utility.setFont(pageTitle);
        try {
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.containerView, fragment, tag).commit();
            navigationView.getMenu().findItem(itemId).setChecked(true);
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (isTaskRoot()) {
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
                        dialog.setContentView(R.layout.dialog_layout);
                        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
                        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
                        Button no = (Button) dialog.findViewById(R.id.dialog_no);
                        tv.setText(utility.getLangauge().equals("bn") ? getString(R.string.exit_message) : "Are you sure want to exit?");
                        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.width = mywidth;
                        ll.setLayoutParams(params);
                        yes.setText(utility.getLangauge().equals("bn") ? getString(R.string.yes) : "Yes");
                        no.setText(utility.getLangauge().equals("bn") ? getString(R.string.no) : "No");
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if(musicService!=null&&musicService.isPlaying()){
                                    musicService.clearNotification();
                                }
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.show();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }

    @Override
    public void changeLanguage() {
        changeNavigationText();
    }

    private void changeNavigationText() {
        String language = utility.getLangauge();
        Menu menu = navigationView.getMenu();
        if (language.equals("bn")) {
            menu.findItem(R.id.nav_home).setTitle(R.string.home_bn);
            menu.findItem(R.id.nav_premium).setTitle(R.string.premium_bn);
            menu.findItem(R.id.nav_free).setTitle(R.string.free_bn);
            menu.findItem(R.id.nav_hits).setTitle(R.string.hits_bn);
            menu.findItem(R.id.nav_selected).setTitle(R.string.selected_bn);
            menu.findItem(R.id.nav_mylist).setTitle(R.string.mylist_bn);
            menu.findItem(R.id.nav_exclusive).setTitle(R.string.exclusive_bn);
            menu.findItem(R.id.nav_newalbum).setTitle(R.string.new_album_bn);
            menu.findItem(R.id.nav_category).setTitle(R.string.catagory_bn);
            menu.findItem(R.id.nav_offline).setTitle(R.string.offline_bn);
            menu.findItem(R.id.nav_notice).setTitle(R.string.notice_bn);
            menu.findItem(R.id.nav_subscription).setTitle(R.string.subscription_bn);
        } else {
            menu.findItem(R.id.nav_home).setTitle(R.string.home);
            menu.findItem(R.id.nav_premium).setTitle(R.string.premium);
            menu.findItem(R.id.nav_free).setTitle(R.string.free);
            menu.findItem(R.id.nav_hits).setTitle(R.string.hits);
            menu.findItem(R.id.nav_selected).setTitle(R.string.selected);
            menu.findItem(R.id.nav_mylist).setTitle(R.string.mylist);
            menu.findItem(R.id.nav_exclusive).setTitle(R.string.exclusive);
            menu.findItem(R.id.nav_newalbum).setTitle(R.string.new_album);
            menu.findItem(R.id.nav_category).setTitle(R.string.catagory);
            menu.findItem(R.id.nav_offline).setTitle(R.string.offline);
            menu.findItem(R.id.nav_notice).setTitle(R.string.notice);
            menu.findItem(R.id.nav_subscription).setTitle(R.string.subscription);
        }
        menu.findItem(R.id.nav_selected).setVisible(false);
        menu.findItem(R.id.nav_exclusive).setVisible(false);
        menu.findItem(R.id.nav_newalbum).setVisible(false);
        if(utility.getConfig().getMode().equals("FREE")){
            menu.findItem(R.id.nav_free).setVisible(false);
            menu.findItem(R.id.nav_premium).setVisible(true);
        }
        else{
            menu.findItem(R.id.nav_free).setVisible(true);
            menu.findItem(R.id.nav_premium).setVisible(false);
        }
        setMiniPlayerInfo();
        searchKeyword.setHint(utility.getLangauge().equals("bn") ? getString(R.string.search) : "Search");
        utility.setMenuFont(menu);
        if(musicService!=null&&musicService.isPlaying()){
            musicService.updateNotification();
        }
    }

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
                        miniPlayer.setVisibility(View.VISIBLE);
                        playerStat.setImageResource(R.drawable.ic_pause);
                    } else {
                        playerStat.setImageResource(R.drawable.ic_play_button);
                    }
                }
                handler.postDelayed(this, 100);
            } catch (Exception ex) {
                utility.logger(ex.toString());
                handler.removeCallbacks(initPlayerStat);
            }
        }
    };

    private void setMiniPlayerInfo() {
        try {
            if (musicService != null && musicService.isPlaying()) {
                SongModel songModel = musicService.getCurrentSong();
                songTitle.setText(
                        utility.getLangauge().equals("bn") ?
                                Html.fromHtml(songModel.getTitle_bn() + " " + getString(R.string.featuring_bn) + " " + songModel.getArtist_bn()) :
                                songModel.getTitle() + " " + getString(R.string.featuring_en) + " " + songModel.getArtist()
                );
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
            musicService = null;
        }
    }

    @Override
    public void songPlaye(int position) {
        //do nothing
    }

    @Override
    public void listPlay(List<SongModel> songModels, int position, boolean isOffline) {
        songModels = utility.filterSong(songModels);
        musicService.playerReset();
        if(musicService.getRepeatOn()){
            musicService.setRepeatOn();
        }
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

    }

    @Override
    public void lastSongDelete() {
        if (musicService != null && musicService.getOfflineStatus()) {
            musicService.pauseSong();
            miniPlayer.setVisibility(View.GONE);
        }
    }

    private void getSingleAlbum(String albumId, String trackId){
        Call<List<AlbumModel>> call = apiInterface.getSingleAlbum(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(),"single_album-"+albumId);
        call.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                if(response.isSuccessful()&&response.code()==200){
                    List<AlbumModel> albumModels = response.body();
                    if(albumModels.size()>0) {
                        Intent intent = new Intent(Main.this, PlayList.class);
                        intent.putExtra("Album", albumModels.get(0));
                        intent.putExtra("trackId", trackId);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(Main.this, PlayList.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                utility.logger(t.toString());
                Intent intent = new Intent(Main.this, PlayList.class);
                startActivity(intent);
            }
        });
    }

    private void navigateApp() {
        if (getIntent().hasExtra("Album")) {
            Intent intent = new Intent(Main.this, PlayList.class);
            intent.putExtra("Album",(AlbumModel)getIntent().getSerializableExtra("Album"));
            intent.putExtra("trackId", getIntent().getExtras().get("trackId").toString());
            startActivity(intent);
        }
        if(getIntent().hasExtra("aid")&&getIntent().hasExtra("tid")){
            getSingleAlbum(getIntent().getExtras().getString("aid"),getIntent().getExtras().getString("tid"));
        }
    }

    private void showVersionDialog(){
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
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        tv.setText(utility.getLangauge().equals("bn") ? Html.fromHtml(utility.getConfig().getVersion_msg_bn()) : utility.getConfig().getVersion_msg_en());
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? getString(R.string.yes) : "Yes");
        if(utility.getConfig().getVersion_update().equals("FORCE")) {
            no.setText(utility.getLangauge().equals("bn") ? getString(R.string.exit) : "Exit");
        }
        else{
            no.setText(utility.getLangauge().equals("bn") ? getString(R.string.no) : "No");
        }
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(utility.isNetworkAvailable()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_store_link)));
                    startActivity(intent);
                    if(utility.getConfig().getVersion_update().equals("FORCE")){
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                }
                else{
                    utility.showToast("No Internet");
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utility.getConfig().getVersion_update().equals("FORCE")){
                    dialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
                else {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void openNotice() {
        drawerLayout.closeDrawers();
        String topTitle = getResources().getString(R.string.notice);
        if (utility.getLangauge().equals("bn")) {
            topTitle = getResources().getString(R.string.notice_bn);
        }
        callFragment(R.id.nav_notice, new Notice(Main.this), KeyWord.NOTICE, topTitle);
    }

    public void openSubscription() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawers();
                String topTitle = getResources().getString(R.string.subscription);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.subscription_bn);
                }
                callFragment(R.id.nav_subscription, new Subscription(Main.this), KeyWord.SUBSCRIPTION, topTitle);
            }
        }, 1000);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            openSubscription();
        }
    };

    BroadcastReceiver bannerBcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String fragmentName = intent.getExtras().getString("name");
            String searchKeyword = "";
            if(intent.hasExtra("keyword")){
                searchKeyword = intent.getExtras().getString("keyword");
            }
            String topTitle = "";
            switch (fragmentName){
                case KeyWord.HOME:
                    topTitle = getResources().getString(R.string.titleEn);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.titleBn);
                    }
                    callFragment(R.id.nav_home, new Home(Main.this, Main.this), KeyWord.HOME, topTitle);
                    break;
                case KeyWord.PREMIUM:
                    topTitle = getResources().getString(R.string.premium);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.premium_bn);
                    }
                    callFragment(R.id.nav_premium, new Premium(Main.this), KeyWord.PREMIUM, topTitle);
                    break;
                case KeyWord.FREE:
                    topTitle = getResources().getString(R.string.free);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.free_bn);
                    }
                    callFragment(R.id.nav_free, new Free(Main.this), KeyWord.FREE, topTitle);
                    break;
                case KeyWord.HITS:
                    topTitle = getResources().getString(R.string.hits);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.hit_bn);
                    }
                    callFragment(R.id.nav_hits, new Hits(Main.this, Main.this), KeyWord.HITS, topTitle);
                    break;
                case KeyWord.MY_LIST:
                    topTitle = getResources().getString(R.string.mylist);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.mylist_bn);
                    }
                    callFragment(R.id.nav_mylist, new MyList(Main.this, Main.this), KeyWord.MY_LIST, topTitle);
                    break;
                case KeyWord.CATEGORY:
                    topTitle = getResources().getString(R.string.catagory);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.catagory_bn);
                    }
                    callFragment(R.id.nav_category, new Category(Main.this), KeyWord.CATEGORY, topTitle);
                    break;
                case KeyWord.OFFLINE:
                    topTitle = getResources().getString(R.string.offline);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.offline_bn);
                    }
                    callFragment(R.id.nav_offline, new Offline(Main.this, Main.this), KeyWord.OFFLINE, topTitle);
                    break;
                case KeyWord.SUBSCRIPTION:
                    topTitle = getResources().getString(R.string.subscription);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.subscription_bn);
                    }
                    callFragment(R.id.nav_subscription, new Subscription(Main.this), KeyWord.SUBSCRIPTION, topTitle);
                    break;
                case KeyWord.NOTICE:
                    topTitle = getResources().getString(R.string.notice);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.notice_bn);
                    }
                    callFragment(R.id.nav_notice, new Notice(Main.this), KeyWord.NOTICE, topTitle);
                    break;
                case KeyWord.LANGUAGE:
                    callFragment(R.id.nav_language, new Language(Main.this, Main.this), KeyWord.LANGUAGE, "Language");
                    break;
                case "SEARCH":
                    navigateSearch(searchKeyword);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("subscription.check");
        registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter bannerIntentFilter = new IntentFilter("banner.click");
        registerReceiver(bannerBcastReceiver, bannerIntentFilter);
        try {
            if(utility.getRefreshState()) {
                changeLanguage();
                String topTitle = getResources().getString(R.string.titleEn);
                if (utility.getLangauge().equals("bn")) {
                    topTitle = getResources().getString(R.string.titleBn);
                }
                pageTitle.setText(topTitle);
                callFragment(R.id.nav_home, new Home(Main.this, this), KeyWord.HOME, topTitle);
                changeNavigationText();
            }
            if (musicService != null && musicService.isPlaying()) {
                setMiniPlayerInfo();
                handler.postDelayed(initPlayerStat, 0);
                songTitle.setSelected(true);
                musicService.updateNotification();
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(bannerBcastReceiver);
        if(musicService!=null&&musicService.isPlaying()){
            musicService.clearNotification();
        }
    }


}
