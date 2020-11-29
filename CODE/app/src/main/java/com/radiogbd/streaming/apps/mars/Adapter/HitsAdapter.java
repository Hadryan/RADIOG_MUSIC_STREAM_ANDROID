package com.radiogbd.streaming.apps.mars.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Html;
import android.util.Log;
import android.view.Display;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.BaseApiClient;
import com.radiogbd.streaming.apps.mars.Http.BaseApiInterface;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.TriangleDrawable;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.HitsModel;
import com.radiogbd.streaming.apps.mars.Model.LikeModel;
import com.radiogbd.streaming.apps.mars.Model.Master;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;
import com.radiogbd.streaming.apps.mars.Service.DownloadService;
import com.squareup.picasso.Picasso;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/5/2017.
 */

public class HitsAdapter extends BaseAdapter {

    private static final String DOWNLOAD = "download";
    private static final String PLAY = "play";
    Context context;
    List<HitsModel> hitsModels;
    List<SongModel> songModels;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    PlayInterface playInterface;
    DB db;
    SubscriptionBox subscriptionBox;
    //long msisdn;

    public static final String SUBS_MOBILE = "mobile";
    public static final String SUBS_STATUS = "status";

    public HitsAdapter(Context context, List<HitsModel> hitsModels, PlayInterface playInterface) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.hitsModels = hitsModels;
        this.playInterface = playInterface;
        db = new DB(this.context);
        initiateSongList();
    }

    private void initiateSongList() {
        songModels = new ArrayList<SongModel>();
        for (int i = 0; i < hitsModels.size(); i++) {
            SongModel songModel = new SongModel();
            songModel.setId(hitsModels.get(i).getId());
            songModel.setAlbum(hitsModels.get(i).getAlbum());
            songModel.setAlbum_bn(hitsModels.get(i).getAlbum_bn());
            songModel.setAlbumImage(hitsModels.get(i).getThumbnail());
            songModel.setArtist(hitsModels.get(i).getArtist());
            songModel.setArtist_bn(hitsModels.get(i).getArtist_bn());
            songModel.setLink(hitsModels.get(i).getLink());
            songModel.setLyrics(hitsModels.get(i).getLyrics());
            songModel.setLyrics_bn(hitsModels.get(i).getLyrics_bn());
            songModel.setTitle(hitsModels.get(i).getTitle());
            songModel.setTitle_bn(hitsModels.get(i).getTitle_bn());
            songModel.setTokenFav(hitsModels.get(i).getTokenFav());
            songModel.setTune(hitsModels.get(i).getTune());
            songModel.setTune_bn(hitsModels.get(i).getTune_bn());
            songModel.setPremium(hitsModels.get(i).getPremium());
            songModels.add(songModel);
        }
        songModels.add(new SongModel(0, "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
    }

    @Override
    public int getCount() {
        return hitsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return hitsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return hitsModels.get(position).getId();
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
        try {
            if (hitsModels.get(position).getId() == 0) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.view_layout, null);
                }
                return convertView;
            } else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.song_layout, null);
                }
                ImageView albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
                final TextView songAlbum = (TextView) convertView.findViewById(R.id.songAlbum);
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
                        .setOnViewListener(new EasyPopup.OnViewListener() {
                            @Override
                            public void initViews(View view, EasyPopup easyPopup) {
                                View arrowView = view.findViewById(R.id.v_arrow);
                                arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.parseColor("#FFFFFF")));
                            }
                        })
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
                                utility.getLangauge().equals("bn") ?
                                        Html.fromHtml(songModels.get(position).getTitle_bn()) + " (" + Html.fromHtml(songModels.get(position).getArtist_bn()) + ")" :
                                        songModels.get(position).getTitle() + " (" + songModels.get(position).getArtist_bn() + ")",
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
                        if (hitsModels.get(position).getTokenFav().equals("No")) {
                            hitsModels.get(position).setTokenFav("Yes");
                        } else {
                            hitsModels.get(position).setTokenFav("No");
                        }
                        setLikeStatus("mylist-" + hitsModels.get(position).getId());
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
                                                context.sendBroadcast(new Intent("subscription.check"));
                                            }
                                        }
                                    }
                                } else {
                                    context.sendBroadcast(new Intent("subscription.check"));
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
                        easyPopup.showAtAnchorView(v, YGravity.BELOW, XGravity.CENTER, 0, -50);
                    }
                });
                Picasso.with(context)
                        .load(context.getString(R.string.image_url) + hitsModels.get(position).getThumbnail())
                        .error(android.R.drawable.screen_background_light_transparent)
                        .placeholder(android.R.drawable.screen_background_light_transparent)
                        .into(albumImage);
                songAlbum.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(hitsModels.get(position).getAlbum_bn())
                                : hitsModels.get(position).getAlbum()
                );
                songArtist.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(context.getResources().getString(R.string.artist_bn) + ": " + hitsModels.get(position).getArtist_bn())
                                : context.getResources().getString(R.string.artist) + ": " + hitsModels.get(position).getArtist()
                );
                songLyrics.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(context.getResources().getString(R.string.lyrics_bn) + ": " + hitsModels.get(position).getLyrics_bn())
                                : context.getResources().getString(R.string.lyrics) + ": " + hitsModels.get(position).getLyrics()
                );
                songTitle.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(hitsModels.get(position).getTitle_bn())
                                : hitsModels.get(position).getTitle()
                );
                songPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                                context.sendBroadcast(new Intent("subscription.check"));
                                            }
                                        }
                                    }
                                } else {
                                    context.sendBroadcast(new Intent("subscription.check"));
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
                                                context.sendBroadcast(new Intent("subscription.check"));
                                            }
                                        }
                                    }
                                } else {
                                    context.sendBroadcast(new Intent("subscription.check"));
                                }
                            } else {
                                playInterface.listPlay(songModels, position, false);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    }
                });
                if (hitsModels.get(position).getTokenFav().equals("No")) {
                    likeButton.setImageResource(R.drawable.ic_heart);
                } else {
                    likeButton.setImageResource(R.drawable.ic_big_heart);
                }
                return convertView;
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
            return convertView;
        }
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
