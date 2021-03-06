package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableGridView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.radiogbd.streaming.apps.mars.Adapter.AlbumAdapter;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.EndlessScrollListener;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
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

public class Free extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    ImageView albumBanner;
    TextView albumText;
    ObservableGridView albumLists;
    RelativeLayout parent;
    List<AlbumModel> albumModels = null;
    AlbumAdapter albumAdapter;
    int bannerWidth;
    int bannerHeight;
    SubscriptionBox subscriptionBox;

    public Free() {
    }

    @SuppressLint("ValidFragment")
    public Free(Context context) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        albumModels = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.premium_layout, null);
        albumBanner = (ImageView) view.findViewById(R.id.albumBanner);
        parent = (RelativeLayout) view.findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        albumText = (TextView) view.findViewById(R.id.albumText);
        albumLists = (ObservableGridView) view.findViewById(R.id.albumLists);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = bannerWidth/3;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        albumBanner.setLayoutParams(params);
        albumBanner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.free));
        getAllFreeAlbum(0);
        albumLists.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: "+(page-1)+", Total Count: "+totalItemsCount);
                if(totalItemsCount%20==0) {
                    getAllFreeAlbum(page - 1);
                }
                return true;
            }
        });
        albumLists.setScrollViewCallbacks(this);
        return view;
    }


    private void getAllFreeAlbum(int page) {
        if(utility.isNetworkAvailable()) {
            //String album = "list_album-" + page;
            //utility.logger(album);
            utility.showProgress(false);
            Call<List<AlbumModel>> call = apiInterface.getFreeList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), String.valueOf(page));
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
        }
        else{
            utility.showToast(context.getString(R.string.no_internet));
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        scrollY = scrollY/2;
        ViewHelper.setTranslationY(albumBanner, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(albumLists, Math.max(0, -scrollY + bannerHeight ));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
