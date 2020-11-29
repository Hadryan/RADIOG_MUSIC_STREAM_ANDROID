package com.radiogbd.streaming.apps.mars.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.TriangleDrawable;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.LikeModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.DownloadService;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/5/2017.
 */

public class SongAdapter extends BaseAdapter {

    private static final String DOWNLOAD = "download";
    private static final String PLAY = "play";
    Context context;
    List<SongModel> songModels;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    ContentApiInterface masterApiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    PlayInterface playInterface;
    DB db;
    SubscriptionBox subscriptionBox;
    private FirebaseAnalytics mFirebaseAnalytics;
    boolean isActivity = true;
    String premiumAction = "";
    //long msisdn;

    public static final String SUBS_MOBILE = "mobile";
    public static final String SUBS_STATUS = "status";

    public SongAdapter(Context context, List<SongModel> songModels, PlayInterface playInterface, String premiumAction) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.songModels = songModels;
        this.songModels.add(new SongModel(0, "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
        this.playInterface = playInterface;
        db = new DB(this.context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.context);
        this.premiumAction = premiumAction;
    }

    @Override
    public int getCount() {
        return songModels.size();
    }

    @Override
    public Object getItem(int position) {
        return songModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songModels.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @SuppressLint("InvalidAnalyticsName")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (songModels.get(position).getId() != 0) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.song_child_layout, null);
                }
                RelativeLayout songLayout = (RelativeLayout) convertView.findViewById(R.id.songLayout);
                TextView songNumber = (TextView) convertView.findViewById(R.id.songNumber);
                TextView songTitle = (TextView) convertView.findViewById(R.id.songTitle);
                TextView songArtist = (TextView) convertView.findViewById(R.id.songArtist);
                ImageView moreOption = (ImageView) convertView.findViewById(R.id.moreOption);
                ImageView premium = (ImageView) convertView.findViewById(R.id.premium);
                ImageView free = (ImageView) convertView.findViewById(R.id.free);
                final ImageView songPlay = (ImageView) convertView.findViewById(R.id.playSong);
                utility.setFont(songNumber);
                songNumber.setTextSize(12);
                utility.setFont(songTitle);
                songTitle.setTextSize(16);
                utility.setFont(songArtist);
                songArtist.setTextSize(12);
                if (utility.getConfig().getMode().equals("FREE")) {
                    if (songModels.get(position).getPremium().equals("Yes")) {
                        premium.setVisibility(View.VISIBLE);
                    } else {
                        premium.setVisibility(View.GONE);
                    }
                } else {
                    if (songModels.get(position).getPremium().equals("Yes")) {
                        free.setVisibility(View.GONE);
                    } else {
                        free.setVisibility(View.VISIBLE);
                    }
                }
                songNumber.setText(utility.getLangauge().equals("bn")
                        ? utility.convertToBangle(String.valueOf(position + 1))
                        : String.valueOf(position + 1));
                songTitle.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(songModels.get(position).getTitle_bn())
                                : songModels.get(position).getTitle()
                );
                songArtist.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(songModels.get(position).getArtist_bn())
                                : songModels.get(position).getArtist()
                );
                EasyPopup easyPopup = EasyPopup.create()
                        .setContext(context)
                        .setContentView(R.layout.custom_popup)
                        .setOnViewListener(new EasyPopup.OnViewListener() {
                            @Override
                            public void initViews(View view, EasyPopup easyPopup) {
                                View arrowView = view.findViewById(R.id.v_arrow);
                                arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.parseColor("#FFFFFF")));
                            }
                        })
                        .setFocusAndOutsideEnable(true)
                        .apply();
                ImageView downloadButton = (ImageView) easyPopup.findViewById(R.id.download);
                ImageView likeButton = (ImageView) easyPopup.findViewById(R.id.like);
                ImageView shareButton = (ImageView) easyPopup.findViewById(R.id.share);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyPopup.dismiss();
                        utility.shareTrack(
                                utility.getLangauge().equals("bn") ?
                                        Html.fromHtml(songModels.get(position).getTitle_bn()) + " (" + Html.fromHtml(songModels.get(position).getArtist_bn()) + ")" :
                                        songModels.get(position).getTitle() + " (" + songModels.get(position).getArtist_bn() + ")",
                                songModels.get(position).getId()
                        );
                    }
                });
                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyPopup.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString("song_id", String.valueOf(songModels.get(position).getId()));
                        bundle.putString("song_title", String.valueOf(songModels.get(position).getTitle()));
                        bundle.putString("song_album", String.valueOf(songModels.get(position).getAlbum()));
                        if (songModels.get(position).getTokenFav().equals("No")) {
                            songModels.get(position).setTokenFav("Yes");
                            bundle.putString("selection_type", "Liked");
                        } else {
                            songModels.get(position).setTokenFav("No");
                            bundle.putString("selection_type", "Unliked");
                        }
                        setLikeStatus("mylist-" + songModels.get(position).getId());
                        notifyDataSetChanged();
                        mFirebaseAnalytics.logEvent("Song Selected", bundle);
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            easyPopup.dismiss();
                            if (songModels.get(position).getPremium().equals("Yes")) {
                                String operator = subscriptionBox.getOperator();
                                if (!operator.equals("NA")) {
                                    if (utility.isNetworkAvailable()) {
                                        if (operator.equals("GP")) {
                                            viewSubscription(songModels.get(position).getId(), position, DOWNLOAD);
                                        } else {
                                            viewSubscription(0, position, DOWNLOAD);
                                        }
                                    } else {
                                        if (operator.equals("GP")) {
                                            if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(position).getId()))) {
                                                downloadSong(position);
                                            } else {
                                                subscriptionBox.activateSubscription(String.valueOf(songModels.get(position).getId()), "0");
                                            }
                                        } else {
                                            if (subscriptionBox.isSubscribed("0")) {
                                                downloadSong(position);
                                            } else {
                                                permormPremiumMove();
                                            }
                                        }
                                    }
                                } else {
                                    permormPremiumMove();
                                }
                            } else {
                                downloadSong(position);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("song_id", String.valueOf(songModels.get(position).getId()));
                            bundle.putString("song_title", String.valueOf(songModels.get(position).getTitle()));
                            bundle.putString("song_album", String.valueOf(songModels.get(position).getAlbum()));
                            bundle.putString("selection_type", "Download");
                            mFirebaseAnalytics.logEvent("Song Selected", bundle);
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    }
                });
                moreOption.setOnClickListener(v -> {
                    easyPopup.showAtAnchorView(moreOption, YGravity.BELOW, XGravity.RIGHT, 0, -40);
                });
                songPlay.setOnClickListener(v -> {
                    try {
                        if (songModels.get(position).getPremium().equals("Yes")) {
                            String operator = subscriptionBox.getOperator();
                            if (!operator.equals("NA")) {
                                if (utility.isNetworkAvailable()) {
                                    if (operator.equals("GP")) {
                                        viewSubscription(songModels.get(position).getId(), position, PLAY);
                                    } else {
                                        viewSubscription(0, position, PLAY);
                                    }
                                } else {
                                    if (operator.equals("GP")) {
                                        if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(position).getId()))) {
                                            playInterface.listPlay(songModels, position, false);
                                        } else {
                                            subscriptionBox.activateSubscription(String.valueOf(songModels.get(position).getId()), "0");
                                        }
                                    } else {
                                        if (subscriptionBox.isSubscribed("0")) {
                                            playInterface.listPlay(songModels, position, false);
                                        } else {
                                            permormPremiumMove();
                                        }
                                    }
                                }
                            } else {
                                permormPremiumMove();
                            }
                        } else {
                            playInterface.listPlay(songModels, position, false);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("song_id", String.valueOf(songModels.get(position).getId()));
                        bundle.putString("song_title", String.valueOf(songModels.get(position).getTitle()));
                        bundle.putString("song_album", String.valueOf(songModels.get(position).getAlbum()));
                        bundle.putString("selection_type", "Play");
                        mFirebaseAnalytics.logEvent("Song Selected", bundle);
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                });
                if (songModels.get(position).getTokenFav().equals("No")) {
                    likeButton.setImageResource(R.drawable.ic_heart);
                } else {
                    likeButton.setImageResource(R.drawable.ic_big_heart);
                }
                songLayout.setOnClickListener(v -> {
                    try {
                        if (songModels.get(position).getPremium().equals("Yes")) {
                            String operator = subscriptionBox.getOperator();
                            if (!operator.equals("NA")) {
                                if (utility.isNetworkAvailable()) {
                                    if (operator.equals("GP")) {
                                        viewSubscription(songModels.get(position).getId(), position, PLAY);
                                    } else {
                                        viewSubscription(0, position, PLAY);
                                    }
                                } else {
                                    if (operator.equals("GP")) {
                                        if (subscriptionBox.isSubscribed(String.valueOf(songModels.get(position).getId()))) {
                                            playInterface.listPlay(songModels, position, false);
                                        } else {
                                            subscriptionBox.activateSubscription(String.valueOf(songModels.get(position).getId()), "0");
                                        }
                                    } else {
                                        if (subscriptionBox.isSubscribed("0")) {
                                            playInterface.listPlay(songModels, position, false);
                                        } else {
                                            permormPremiumMove();
                                        }
                                    }
                                }
                            } else {
                                permormPremiumMove();
                            }
                        } else {
                            playInterface.listPlay(songModels, position, false);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("song_id", String.valueOf(songModels.get(position).getId()));
                        bundle.putString("song_title", String.valueOf(songModels.get(position).getTitle()));
                        bundle.putString("song_album", String.valueOf(songModels.get(position).getAlbum()));
                        bundle.putString("selection_type", "Play");
                        mFirebaseAnalytics.logEvent("Song Selected", bundle);
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                });

            } else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.view_layout, null);
                }
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
        return convertView;
    }

    /*
    * This function move user to Subscription Page, according to the root
    * of the SongAdapter.
    * If SongAdapter comes from Activity, then it SendBroadcast and Finish Current Activity
    * If SongAdapter comes from Fragment, then is SendBroadcast
    * If SongAdapter comes from Dialog, then it just show Toast
    * */
    private void permormPremiumMove() {
        switch (premiumAction) {
            case KeyWord.ACTIVITY:
                context.sendBroadcast(new Intent("subscription.check"));
                ((Activity) context).finish();
                break;
            case KeyWord.FRAGMENT:
                context.sendBroadcast(new Intent("subscription.check"));
                break;
            case KeyWord.DIALOG:
                utility.showToast("Subscription Required");
        }
    }

    public void viewSubscription(int trackId, int position, String operation) {
        utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.viewstatus(utility.getAuthorization(), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), String.valueOf(trackId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utility.hideProgress();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        subscriptionBox.setSubscription(String.valueOf(trackId), content.toString());
                        if (subscriptionBox.isSubscribed(String.valueOf(trackId))) {
                            switch (operation) {
                                case DOWNLOAD:
                                    downloadSong(position);
                                    break;
                                case PLAY:
                                    playInterface.listPlay(songModels, position, false);
                                    break;
                            }
                        } else {
                            if (subscriptionBox.getOperator().equals("GP")) {
                                subscriptionBox.activateSubscription(String.valueOf(songModels.get(position).getId()), "0");
                            } else {
                                context.sendBroadcast(new Intent("subscription.check"));
                                if (isActivity) {
                                    ((Activity) context).finish();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.hideProgress();
                utility.logger(t.toString());
            }
        });
    }


    private void downloadSong(int position) {
        utility.logger("Total Internal Memomry Size => " + utility.getTotalInternalMemorySize());
        utility.logger("Available Internal Memory size => " + utility.getAvailableInternalMemorySize());
        long fileSize = utility.checkFileSize(context.getString(R.string.image_url) + songModels.get(position).getLink());
        long availableSize = utility.getAvailableInternalMemorySize();
        if (fileSize < availableSize) {
            try {
                db.open();
                if (utility.checkIfSongExists(songModels.get(position))) {
                    utility.showToast("Already Downloaded");
                } else {
                    if (db.insertSong(songModels.get(position))) {
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("song", songModels.get(position));
                        context.startService(intent);
                    } else {
                        utility.logger("Data not inserted");
                    }
                    db.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            utility.showToast("No Space");
        }
    }

    /*public void viewBkashStatus(int trackId, final int position, String method){
        utility.showProgress(true);
        Call<List<Bkash>> call = masterApiInterface.bkashViewStatus(utility.getAuthorization(), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<Bkash>>() {
            @Override
            public void onResponse(Call<List<Bkash>> call, Response<List<Bkash>> response) {
                utility.hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    List<Bkash> bkashes = response.body();
                    Bkash b = bkashes.get(0);
                    utility.setBkashSubscription(b);
                    if(utility.isSubscribed(trackId)) {
                        if (method.equals(KeyWord.PLAY)) {
                            playInterface.listPlay(songModels, position, false);
                        } else {
                            downloadSong(position);
                        }
                    }
                    else{
                        showPremiumDialog(trackId);
                    }
                }
                else{
                    utility.showToast(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Bkash>> call, Throwable t) {
                utility.hideProgress();
            }
        });
    }*/

    /*private void checkMasterSubscription(int trackId, final int position, String method){
        utility.showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.viewstatus(context.getString(R.string.authorization_key), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken(), String.valueOf(trackId));
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    utility.hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        utility.writeSubscriptionStatus(trackId, master);
                        if(utility.isSubscribed(trackId)) {
                            if (method.equals(KeyWord.PLAY)) {
                                playInterface.listPlay(songModels, position, false);
                            } else {
                                downloadSong(position);
                            }
                        }
                        else{
                            showPremiumDialog(trackId);
                        }
                    }
                    else{
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            utility.hideProgress();
            utility.logger(ex.toString());
        }
    }*/

    /*private void showPremiumDialog(int trackId){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        if(!utility.getOperator().equals("BK")) {
            switch (utility.getMdn()) {
                case "18":
                    tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                    break;
                case "16":
                    tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                    break;
                case "13":
                    tv.setText(utility.getLangauge().equals("bn") ? "এই গানটি শুনতে/ডাউনলোড করতে আপনার জিপি ব্যালান্স থেকে" + utility.getPrice(trackId) + " টাকা চার্জ প্রযোজ্য। আপনি কি আগ্রহী?" : "To listen/download the song charge " + utility.getPrice(trackId) + " Taka applicable from your GP balance. Are you sure want to proceed?");
                    break;
                case "17":
                    tv.setText(utility.getLangauge().equals("bn") ? "এই গানটি শুনতে/ডাউনলোড করতে আপনার জিপি ব্যালান্স থেকে" + utility.getPrice(trackId) + " টাকা চার্জ প্রযোজ্য। আপনি কি আগ্রহী?" : "To listen/download the song charge " + utility.getPrice(trackId) + " Taka applicable from your GP balance. Are you sure want to proceed?");
                    break;
                case "19":
                    tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bangalink_premium_message_bn) : context.getString(R.string.banglalink_premium_message_en));
                    break;
            }
        }
        else{
            tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bkash_premium_message_bn) : context.getString(R.string.bkash_premium_message_en));
        }
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
        no.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!utility.getOperator().equals("BK")) {
                    //activateSubscription(String.valueOf(trackId), "0");
                }
                else{
                    activeBkash();
                }
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
    }*/

    /*public void activeBkash(){
        utility.showProgress(true);
        Call<List<Bkash>> call = masterApiInterface.bkashActivation(utility.getAuthorization(), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<Bkash>>() {
            @Override
            public void onResponse(Call<List<Bkash>> call, Response<List<Bkash>> response) {
                utility.hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    List<Bkash> bkashes = response.body();
                    Bkash b = bkashes.get(0);
                    utility.setBkashSubscription(b);
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("url", b.getUrl());
                    context.startActivity(intent);
                }
                else{
                    utility.showToast(String.valueOf(response.code()));
                    //initiateView();
                }
            }

            @Override
            public void onFailure(Call<List<Bkash>> call, Throwable t) {
                utility.hideProgress();
                //initiateView();
            }
        });
    }*/

    /*private void activateSubscription(String trackId, String pin){
        utility.showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.activation(context.getString(R.string.authorization_key), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken(), trackId, pin);
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    utility.hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        utility.writeSubscriptionStatus(Integer.parseInt(trackId), master);
                        if (master.getComment().equals("PIN Request Success")) {
                            validatePinDialog(trackId);
                        } else if (master.getComment().equals("Charge Request Success")||master.getComment().equals("Request Under Process")) {
                            showConfirmation();
                        } else {
                            utility.showToast("PIN Process Failed");
                        }
                    }
                    else{
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            utility.hideProgress();
        }
    }*/

/*    private void validatePinDialog(String trackId) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            RadioGroup rgOperator = (RadioGroup)dialog.findViewById(R.id.rg_operator);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            TextView phoneCode = (TextView) dialog.findViewById(R.id.phone_code);
            phoneCode.setVisibility(View.GONE);
            rgOperator.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_pin_bn) : context.getString(R.string.number_pin_en));
            phoneNumber.setHint("PIN e.g. XXXX");
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = phoneNumber.getText().toString();
                    if(pin.length()>0) {
                        dialog.dismiss();
                        //activateSubscription(trackId, phoneNumber.getText().toString());
                    }
                    else{
                        utility.showToast("Pin Required");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            utility.showToast(ex.toString());
        }
    }

    private void showConfirmation(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        switch (utility.getMdn()){
            case "16":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "17":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_confirmation_msg_bn) : context.getString(R.string.gp_confirmation_msg_en));
                break;
            case "18":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "19":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.banglalink_confirmation_msg_bn) : context.getString(R.string.banglalink_confirmation_msg_en));
                break;
        }
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
    }*/

    private void setLikeStatus(String liked) {
        utility.showProgress(true);
        Call<LikeModel> call = apiInterface.getLikeStatus(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), liked);
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
            }
        });
    }
}
