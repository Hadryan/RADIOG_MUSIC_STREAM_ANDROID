package com.radiogbd.streaming.apps.mars.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.LikeModel;
import com.radiogbd.streaming.apps.mars.Model.SelectedModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.DownloadService;
import com.squareup.picasso.Picasso;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/5/2017.
 */

public class SelectedAdapter extends BaseAdapter {

    Context context;
    List<SelectedModel> selectedModels;
    List<SongModel> songModels;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    PlayInterface playInterface;
    DB db;
    SubscriptionBox subscriptionBox;
    //long msisdn;

    public static final String SUBS_MOBILE = "mobile";
    public static final String SUBS_STATUS = "status";

    public SelectedAdapter(Context context, List<SelectedModel> selectedModels, PlayInterface playInterface){
        this.context = context;
        utility = new Utility(this.context);
        this.selectedModels = selectedModels;
        this.playInterface = playInterface;
        db = new DB(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        initiateSongList();
    }

    private void initiateSongList(){
        songModels = new ArrayList<SongModel>();
        for(int i=0; i<selectedModels.size(); i++){
            SongModel songModel = new SongModel();
            songModel.setId(selectedModels.get(i).getId());
            songModel.setAlbum(selectedModels.get(i).getAlbum());
            songModel.setAlbum_bn(selectedModels.get(i).getAlbum_bn());
            songModel.setAlbumImage(selectedModels.get(i).getThumbnail());
            songModel.setArtist(selectedModels.get(i).getArtist());
            songModel.setArtist_bn(selectedModels.get(i).getArtist_bn());
            songModel.setLink(selectedModels.get(i).getLink());
            songModel.setLyrics(selectedModels.get(i).getLyrics());
            songModel.setLyrics_bn(selectedModels.get(i).getLyrics_bn());
            songModel.setTitle(selectedModels.get(i).getTitle());
            songModel.setTitle_bn(selectedModels.get(i).getTitle_bn());
            songModel.setTokenFav(selectedModels.get(i).getTokenFav());
            songModel.setTune(selectedModels.get(i).getTune());
            songModel.setTune_bn(selectedModels.get(i).getTune_bn());
            songModel.setPremium(selectedModels.get(i).getPremium());
            songModels.add(songModel);
        }
        songModels.add(new SongModel(0,"","","","","","","","","","","","","",""));
    }

    @Override
    public int getCount() {
        return selectedModels.size();
    }

    @Override
    public Object getItem(int position) {
        return selectedModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return selectedModels.get(position).getId();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            if(songModels.get(position).getId()==0){
                if(convertView==null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.view_layout, null);
                }
            }
            else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.song_layout, null);
                }
                ImageView albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
                TextView songAlbum = (TextView) convertView.findViewById(R.id.songAlbum);
                TextView songArtist = (TextView) convertView.findViewById(R.id.songArtist);
                TextView songLyrics = (TextView) convertView.findViewById(R.id.songLyrics);
                TextView songTitle = (TextView) convertView.findViewById(R.id.songTitle);
                ImageView moreOption = (ImageView) convertView.findViewById(R.id.moreOption);
                ImageView songPlay = (ImageView) convertView.findViewById(R.id.songPlay);
                ImageView premium = (ImageView) convertView.findViewById(R.id.premium);
                LinearLayout cardBase = (LinearLayout) convertView.findViewById(R.id.cardBase);
                utility.setFont(songArtist);
                songArtist.setTextSize(12);
                utility.setFont(songLyrics);
                songLyrics.setTextSize(12);
                utility.setFont(songTitle);
                songTitle.setTextSize(16);
                utility.setFont(songAlbum);
                songAlbum.setTextSize(16);
                EasyPopup easyPopup = EasyPopup.create()
                        .setContext(context)
                        .setContentView(R.layout.custom_popup)
                        //.setAnimationStyle(R.style.TopPopAnim)
                        .setFocusAndOutsideEnable(true)
                        .apply();
                ImageView downloadButton = (ImageView) easyPopup.getContentView().findViewById(R.id.download);
                ImageView likeButton = (ImageView) easyPopup.getContentView().findViewById(R.id.like);
                ImageView shareButton = (ImageView) easyPopup.getContentView().findViewById(R.id.share);
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyPopup.dismiss();
                        utility.shareTrack(
                                utility.getLangauge().equals("bn")?
                                        Html.fromHtml(songModels.get(position).getTitle_bn())+" ("+Html.fromHtml(songModels.get(position).getArtist_bn())+")":
                                        songModels.get(position).getTitle()+" ("+songModels.get(position).getArtist_bn()+")",
                                songModels.get(position).getId()
                        );
                    }
                });
                if (songModels.get(position).getPremium().equals("Yes")) {
                    premium.setVisibility(View.VISIBLE);
                } else {
                    premium.setVisibility(View.GONE);
                }
                likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyPopup.dismiss();
                        if (selectedModels.get(position).getTokenFav().equals("No")) {
                            selectedModels.get(position).setTokenFav("Yes");
                        } else {
                            selectedModels.get(position).setTokenFav("No");
                        }
                        setLikeStatus("mylist-" + selectedModels.get(position).getId());
                        initiateSongList();
                        notifyDataSetChanged();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            easyPopup.dismiss();
                            if (songModels.get(position).getPremium().equals("Yes")) {
                                if (songModels.get(position).getPremium().equals("Yes")) {
                                    /*if (utility.getMdn().equals("00")) {
                                        utility.makeSubscriptionDialog(false);
                                    } else {
                                        //checkMasterSubscription(songModels.get(position).getId(), position, KeyWord.DOWNLOAD);
                                    }*/
                                } else {
                                    playInterface.listPlay(songModels, position, false);
                                }
                            } else {
                                downloadSong(position);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    }
                });
                moreOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        easyPopup.showAtAnchorView(v, YGravity.BELOW, XGravity.CENTER, 0, 0);
                    }
                });
            Picasso.with(context)
                    .load(context.getString(R.string.image_url) + selectedModels.get(position).getThumbnail())
                    .error(android.R.drawable.screen_background_light_transparent)
                    .placeholder(android.R.drawable.screen_background_light_transparent)
                    .into(albumImage);
//            Ion.with(context)
//                    .load(context.getString(R.string.image_url)+selectedModels.get(position).getThumbnail())
//                    .withBitmap()
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.broken_image)
//                    .intoImageView(albumImage);
                /*Glide.with(context)
                        .load(context.getString(R.string.image_url) + selectedModels.get(position).getThumbnail())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(80, 80)
                        .into(new BitmapImageViewTarget(albumImage) {
                            @Override
                            public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                                super.onResourceReady(drawable, anim);
                                loadingImage.setVisibility(View.GONE);
                            }
                        });*/
                //songAlbum.setText(selectedModels.get(position).getAlbum());
                songAlbum.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(selectedModels.get(position).getAlbum_bn())
                                : selectedModels.get(position).getAlbum()
                );
                //songArtist.setText("Artist: "+selectedModels.get(position).getArtist());
                songArtist.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(context.getResources().getString(R.string.artist_bn) + ": " + selectedModels.get(position).getArtist_bn())
                                : context.getResources().getString(R.string.artist) + ": " + selectedModels.get(position).getArtist()
                );
                //songLyrics.setText("Lyrics: "+selectedModels.get(position).getLyrics());
                songLyrics.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(context.getResources().getString(R.string.lyrics_bn) + ": " + selectedModels.get(position).getLyrics_bn())
                                : context.getResources().getString(R.string.lyrics) + ": " + selectedModels.get(position).getLyrics()
                );
                //songTitle.setText(selectedModels.get(position).getTitle());
                songTitle.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(selectedModels.get(position).getTitle_bn())
                                : selectedModels.get(position).getTitle()
                );
//            songLiked.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(selectedModels.get(position).getTokenFav().equals("No")){
//                        selectedModels.get(position).setTokenFav("Yes");
//                    }
//                    else{
//                        selectedModels.get(position).setTokenFav("No");
//                    }
//                    setLikeStatus("mylist-"+selectedModels.get(position).getId());
//                    notifyDataSetChanged();
//                }
//            });
                songPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (songModels.get(position).getPremium().equals("Yes")) {
                                if (songModels.get(position).getPremium().equals("Yes")) {
                                    /*if (utility.getMdn().equals("00")) {
                                        utility.makeSubscriptionDialog(false);
                                    } else {
                                        //checkMasterSubscription(songModels.get(position).getId(), position, KeyWord.PLAY);
                                    }*/
                                } else {
                                    playInterface.listPlay(songModels, position, false);
                                }
                            } else {
                                playInterface.listPlay(songModels, position, false);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    }
                });
                cardBase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (songModels.get(position).getPremium().equals("Yes")) {
                                /*if (utility.getMdn().equals("00")) {
                                    utility.makeSubscriptionDialog(false);
                                } else {
                                    //checkMasterSubscription(songModels.get(position).getId(), position, KeyWord.PLAY);
                                }*/
                            } else {
                                playInterface.listPlay(songModels, position, false);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    }
                });
                if (selectedModels.get(position).getTokenFav().equals("No")) {
                    likeButton.setImageResource(R.drawable.ic_heart);
                } else {
                    likeButton.setImageResource(R.drawable.ic_big_heart);
                }
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return convertView;
    }

    private void downloadSong(int position) {
        utility.logger("Total Internal Memomry Size => "+utility.getTotalInternalMemorySize());
        utility.logger("Available Internal Memory size => "+utility.getAvailableInternalMemorySize());
        long fileSize = utility.checkFileSize(context.getString(R.string.image_url)+songModels.get(position).getLink());
        long availableSize = utility.getAvailableInternalMemorySize();
        if(fileSize<availableSize){
            try {
                db.open();
                if(utility.checkIfSongExists(songModels.get(position))){
                    utility.showToast("Already Downloaded");
                }
                else {
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
        }
        else{
            utility.showToast("No Space");
        }
    }

    /*private void checkMasterSubscription(int trackId, final int position, String method){
        utility.showProgress(false);
        try{
            Call<List<Master>> call = apiInterface.viewstatus(context.getString(R.string.authorization_key), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken(), String.valueOf(trackId));
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
        switch (utility.getMdn()){
            case "18":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "16":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "17":
                tv.setText(utility.getLangauge().equals("bn") ? "এই গানটি শুনতে/ডাউনলোড করতে আপনার জিপি ব্যালান্স থেকে "+utility.getPrice(trackId)+" টাকা চার্জ প্রযোজ্য। আপনি কি আগ্রহী?" : "To listen/download the song charge "+utility.getPrice(trackId)+" Taka applicable from your GP balance. Are you sure want to proceed?");
                break;
            case "19":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bangalink_premium_message_bn) : context.getString(R.string.banglalink_premium_message_en));
                break;
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
                //activateSubscription(String.valueOf(trackId),"0");
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

    /*private void activateSubscription(String trackId, String pin){
        utility.showProgress(false);
        try{
            Call<List<Master>> call = apiInterface.activation(context.getString(R.string.authorization_key), utility.getOperator(), utility.getMsisdn(), utility.getFirebaseToken(), trackId, pin);
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

    private void validatePinDialog(String trackId) {
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
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            TextView phoneCode = (TextView) dialog.findViewById(R.id.phone_code);
            phoneCode.setVisibility(View.GONE);
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

    /*private void showConfirmation(){
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
        //tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
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

    /*private void checkSubscription(RequestBody requestBody, final int position, String method) {
        utility.showProgress();
        try {
            Call<ResponseBody> call = baseApiInterface.checkStatus(context.getString(R.string.robi_athurization_key), requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.d("RESULT", jsonObject.toString());
                            if (jsonObject.optInt("code") == 200) {
                                if(!jsonObject.optString("cause").equals("None")) {
                                    //audioPlayerView(position);
                                    utility.writeSubscriptionStatus(songModels.get(position).getId(),Long.parseLong(jsonObject.optString("expireTime")));
                                    if (method.equals(KeyWord.PLAY)) {
                                        playInterface.listPlay(songModels, position, false);
                                    } else {
                                        downloadSong(position);
                                    }
                                }
                                else{
                                    utility.writeSubscriptionStatus(songModels.get(position).getId(),Long.parseLong(jsonObject.optString("expireTime")));
                                    showPremiumDialog();
                                }
                            }
                            else{
                                utility.showToast("You are not Subscribed");
                            }

                        } catch (Exception ex) {
                            Log.d("RESULT", ex.toString());
                        }
                    } else {
                        utility.showToast("Response not found");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("RESULT", t.toString());
                    utility.hideProgress();
                }
            });
        }
        catch (Exception ex){
            Log.d("TAG",ex.toString());
        }
    }*/

    /*private void showPremiumDialog(){
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
            case "18":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "16":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "19":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bangalink_premium_message_bn) : context.getString(R.string.banglalink_premium_message_en));
                break;
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
                switch (utility.getMdn()){
                    case "18":
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mdn", utility.getMsisdn());
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                            makeSubscription(body);
                        }
                        catch (Exception ex){
                            utility.logger(ex.toString());
                        }
                        break;
                    case "16":
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mdn", utility.getMsisdn());
                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                            makeSubscription(body);
                        }
                        catch (Exception ex){
                            utility.logger(ex.toString());
                        }
                        break;
                    case "19":
                        makeBanglalinkSubscription();
                        break;
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
    }

    private void makeSubscription(RequestBody requestBody) {
        utility.showProgress();
        try {
            Call<ResponseBody> call = baseApiInterface.makeWeeklySubscription(context.getString(R.string.robi_athurization_key), requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        showConfirmation();
                    } else {
                        utility.showToast("Subscription Failed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("RESULT", t.toString());
                    utility.hideProgress();
                }
            });
        }
        catch (Exception ex){
            Log.d("TAG",ex.toString());
        }
    }*/

    /*private void checkSubscriptionBanglalink(final int position, String method) {
        utility.showProgress();
        try {
            Call<ResponseBody> call = apiInterface.viewStatus(context.getString(R.string.authorization_key), String.valueOf(utility.getMsisdn()), utility.getFirebaseToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            String result = response.body().string();
                            utility.logger(result);
                            if(result.length()>0){
                                utility.writeSubscriptionStatus(songModels.get(position).getId(),Long.parseLong(result));
                                if (method.equals(KeyWord.PLAY)) {
                                    playInterface.listPlay(songModels, position, false);
                                } else {
                                    downloadSong(position);
                                }
                            }
                            else if(result.length()==0){
                                utility.writeSubscriptionStatus(songModels.get(position).getId(),Long.parseLong("0"));
                                showPremiumDialog();
                            }
                            else{
                                utility.showToast("You are not Subscribed");
                            }
                        } catch (Exception ex) {
                            Log.d("RESULT", ex.toString());
                        }
                    } else {
                        utility.showToast("Response not found");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("RESULT", t.toString());
                    utility.hideProgress();
                }
            });
        }
        catch (Exception ex){
            Log.d("TAG",ex.toString());
        }
    }*/

    /*private void makeBanglalinkSubscription() {
        utility.showProgress();
        try {
            Call<ResponseBody> call = apiInterface.activateBlink(context.getString(R.string.authorization_key), String.valueOf(utility.getMsisdn()), utility.getFirebaseToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    utility.hideProgress();
                    try {
                        if (response.isSuccessful() && response.code() == 200) {
                            String result = response.body().string();
                            if(result.equals("SubscriptionSuccess")) {
                                showConfirmation();
                            }
                            else if(result.equals("SubscriptionFailed")){
                                utility.showToast("Subscription Failed");
                            }
                            else if(result.equals("AlreadySubscribed")){
                                utility.showToast("Already Subscribed");
                            }
                        } else {
                            utility.showToast("Subscription Failed");
                        }
                    }
                    catch (Exception ex){
                        utility.logger(ex.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("RESULT", t.toString());
                    utility.hideProgress();
                }
            });
        }
        catch (Exception ex){
            Log.d("TAG",ex.toString());
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
        tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
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

    /*private void checkInitialSubscription(int position, String method) {
        String phone, status, message;
        phone = status = message = "";
        HashMap<String, String> map = new HashMap<>();
        map = utility.getSubscriptionData();
        if (map.size() > 0) {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) map.get(key);
                switch (key) {
                    case SUBS_MOBILE:
                        phone = value;
                        break;
                    case SUBS_STATUS:
                        status = value;
                        break;
                }
            }
            if (status.equals("success")) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject jsonAuth = new JSONObject();
                    jsonAuth.put("username", "gseries");
                    jsonAuth.put("api_key", "yIraI2Q5k3U79FHv");
                    jsonAuth.put("api_secret", "W!>1/{8,&TVbVhWK");
                    JSONObject request = new JSONObject();
                    request.put("request", "STATUS");
                    request.put("mobile", phone);
                    jsonObject.put("auth", jsonAuth);
                    jsonObject.put("request_data", request);
                    String postBody = jsonObject.toString();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
                    checkSubscription(body, position, method);
                } catch (Exception ex) {
                    utility.showToast(ex.toString());
                }
            } else {
                utility.checkInfo();
            }
        } else {
            utility.checkInfo();
        }
    }*/

    /*private void checkSubscription(RequestBody requestBody, final int position, String method) {
        utility.showProgress();
        try {
            Call<ResponseBody> call = baseApiInterface.getSubscription(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Log.d("RESULT", jsonObject.toString());
                            JSONObject jsonResult = jsonObject.optJSONObject("result");
                            if (jsonResult.optString("status").equals("success")) {
                                //audioPlayerView(position);
                                if(method.equals(KeyWord.PLAY)) {
                                    playInterface.listPlay(songModels, position, false);
                                }
                                else{
                                    downloadSong(position);
                                }
                            }
                            else{
                                utility.checkInfo();
                            }

                        } catch (Exception ex) {
                            Log.d("RESULT", ex.toString());
                        }
                    } else {
                        utility.showToast("Response not found");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("RESULT", t.toString());
                    utility.hideProgress();
                }
            });
        }
        catch (Exception ex){
            Log.d("TAG",ex.toString());
        }
    }*/

    private void setLikeStatus(String liked){
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
