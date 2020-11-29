package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.radiogbd.streaming.apps.mars.Adapter.AlbumAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.ArtistAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.EndlessScrollListener;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.ArtistModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/12/2017.
 */

public class AllArtist extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    ImageView albumBanner;
    TextView artistText;
    ObservableGridView artistLists;
    RelativeLayout parent;
    List<ArtistModel> artistModels = null;
    ArtistAdapter artistAdapter;
    int bannerWidth;
    int bannerHeight;
    SubscriptionBox subscriptionBox;

    public AllArtist() {
    }

    @SuppressLint("ValidFragment")
    public AllArtist(Context context) {
        this.context = context;
        utility = new Utility(this.context);
        artistModels = new ArrayList<>();
        subscriptionBox = new SubscriptionBox(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allartist_layout, null);
        albumBanner = (ImageView) view.findViewById(R.id.albumBanner);
        artistText = (TextView) view.findViewById(R.id.artistText);
        artistLists = (ObservableGridView) view.findViewById(R.id.artistLists);
        parent = (RelativeLayout) view.findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = bannerWidth / 3;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        albumBanner.setLayoutParams(params);
        //albumBanner.setImageResource(utility.getResourceId("All Artist"));
        Glide.with(context)
                .load(context.getString(R.string.cat_image_url) + "all_artist.jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.rg)
                .placeholder(R.drawable.loadin_g)
                .into(new BitmapImageViewTarget(albumBanner) {
                    @Override
                    public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                    }
                });
        getAllArtist(0);
        artistLists.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: " + (page - 1) + ", Total Count: " + totalItemsCount);
                if (totalItemsCount % 20 == 0) {
                    getAllArtist(page - 1);
                }
                return true;
            }
        });
        artistLists.setScrollViewCallbacks(this);
        return view;
    }


    private void getAllArtist(int page) {
        if (utility.isNetworkAvailable()) {
            String album = "list_artist-" + page;
            utility.logger(album);
            utility.showProgress(false);
            Call<List<ArtistModel>> call = apiInterface.getArtists(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), album);
            call.enqueue(new Callback<List<ArtistModel>>() {
                @Override
                public void onResponse(Call<List<ArtistModel>> call, Response<List<ArtistModel>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            List<ArtistModel> models = response.body();
                            artistModels.addAll(models);
                            utility.logger("Highlight List Size = " + artistModels.size());
                            if (artistModels.size() > 0) {
                                artistText.setVisibility(View.GONE);
                                artistLists.setVisibility(View.VISIBLE);
                                artistAdapter = new ArtistAdapter(context, artistModels);
                                artistAdapter.notifyDataSetChanged();
                                artistLists.setAdapter(artistAdapter);
                                artistLists.setSelection(artistModels.size() > 20 ? artistModels.size() - models.size() - 6 : 0);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<ArtistModel>> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        scrollY = scrollY / 2;
        ViewHelper.setTranslationY(albumBanner, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(artistLists, Math.max(0, -scrollY + bannerHeight));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
