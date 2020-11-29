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
import android.widget.LinearLayout;
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
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.EndlessScrollListener;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.GenreModel;
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

public class CategoryAlbum extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    ImageView albumBanner;
    TextView albumText;
    ObservableGridView albumLists;
    List<AlbumModel> albumModels = null;
    AlbumAdapter albumAdapter;
    GenreModel genreModel;
    int bannerWidth;
    int bannerHeight;
    SubscriptionBox subscriptionBox;

    public CategoryAlbum() {
    }

    @SuppressLint("ValidFragment")
    public CategoryAlbum(Context context, GenreModel genreModel) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        albumModels = new ArrayList<>();
        this.genreModel = genreModel;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allalbum_layout, null);
        albumBanner = (ImageView) view.findViewById(R.id.albumBanner);
        albumText = (TextView) view.findViewById(R.id.albumText);
        albumLists = (ObservableGridView) view.findViewById(R.id.albumLists);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = bannerWidth / 3;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        albumBanner.setLayoutParams(params);
        //albumBanner.setImageResource(utility.getResourceId(genreModel.getTitle()));
        Glide.with(context)
                .load(context.getString(R.string.cat_image_url) + genreModel.getId() + ".jpg")
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
        //albumBanner.setImageResource(utility.getResourceId(genreModel.getTitle()));
        albumBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing
            }
        });
        albumLists.setScrollViewCallbacks(this);
        getAllAlbumByCategory(0);
        albumLists.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: " + (page - 1) + ", Total Count: " + totalItemsCount);
                if (totalItemsCount % 20 == 0) {
                    getAllAlbumByCategory(page - 1);
                }
                return true;
            }
        });
        return view;
    }


    private void getAllAlbumByCategory(int page) {
        if (utility.isNetworkAvailable()) {
            String album = "list_genre-" + genreModel.getId() + "-" + page;
            utility.logger(album);
            utility.showProgress(false);
            Call<List<AlbumModel>> call = apiInterface.getAlbumByCategory(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), album);
            call.enqueue(new Callback<List<AlbumModel>>() {
                @Override
                public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            List<AlbumModel> models = response.body();
                            albumModels.addAll(models);
                            utility.logger("Highlight List Size = " + albumModels.size());
                            if (albumModels.size() > 0) {
                                albumText.setVisibility(View.GONE);
                                albumLists.setVisibility(View.VISIBLE);
                                albumAdapter = new AlbumAdapter(context, albumModels);
                                albumAdapter.notifyDataSetChanged();
                                albumLists.setAdapter(albumAdapter);
                                albumLists.setSelection(albumModels.size() > 20 ? albumModels.size() - models.size() - 6 : 0);
                            }
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
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
        ViewHelper.setTranslationY(albumLists, Math.max(0, -scrollY + bannerHeight));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
