package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.radiogbd.streaming.apps.mars.Adapter.HitsAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.HitsModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Hits extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    TextView hitsTitle;
    LinearLayout hitsLabel;
    ObservableListView hitsList;
    ImageView hitsBanner;
    List<HitsModel> hitsModels = null;
    HitsAdapter hitsAdapter;
    int bannerWidth;
    int bannerHeight;
    PlayInterface playInterface;
    SubscriptionBox subscriptionBox;

    public Hits() {
    }

    @SuppressLint("ValidFragment")
    public Hits(Context context, PlayInterface playInterface) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hits_layout, null);
        hitsTitle = (TextView) view.findViewById(R.id.hitsTitle);
        hitsList = (ObservableListView) view.findViewById(R.id.hitLists);
        hitsBanner = (ImageView) view.findViewById(R.id.hitsBanner);
        hitsLabel = (LinearLayout) view.findViewById(R.id.hitListLabel);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = (bannerWidth / 8) * 5;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        hitsBanner.setLayoutParams(params);
        RelativeLayout.LayoutParams labelParam = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight / 3);
        hitsLabel.setLayoutParams(labelParam);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//
//        }
        hitsList.setScrollViewCallbacks(this);
        getAllHits();
        return view;
    }

    private void getAllHits() {
        if(utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<HitsModel>> call = apiInterface.getHitsList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
                call.enqueue(new Callback<List<HitsModel>>() {
                    @Override
                    public void onResponse(Call<List<HitsModel>> call, Response<List<HitsModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                hitsModels = response.body();
                                utility.logger("Hits List Size = " + hitsModels.size());
                                if (hitsModels.size() > 0) {
                                    for(int i=0; i<context.getResources().getInteger(R.integer.list_min_data_count); i++){
                                        hitsModels.add(new HitsModel(0));
                                    }
                                    hitsAdapter = new HitsAdapter(context, hitsModels, playInterface);
                                    hitsAdapter.notifyDataSetChanged();
                                    hitsList.setAdapter(hitsAdapter);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<HitsModel>> call, Throwable t) {
                        utility.hideProgress();
                        utility.logger(t.toString());
                        utility.showToast(context.getString(R.string.http_error));
                    }
                });
            } catch (Exception e) {
                utility.logger(e.toString());
            }
        }
        else{
            utility.showToast(context.getString(R.string.no_internet));
        }
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        scrollY = scrollY/2;
        ViewHelper.setTranslationY(hitsBanner, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(hitsList, Math.max(0, -scrollY + bannerHeight));
        if (scrollY < (bannerHeight / 2)) {
            ViewHelper.setAlpha(hitsLabel, 0);
        } else if (scrollY >= (bannerHeight / 2) && scrollY <= (bannerHeight / 3) * 2) {
            int alphaValue = ((bannerHeight / 3) * 2) - (bannerHeight / 2);
            int scaleValue = ((bannerHeight / 3) * 2) - scrollY;
            String value = String.format("%.1f", 1.0 - (float) scaleValue / alphaValue);
            double alpha = Double.parseDouble(value);
            ViewHelper.setAlpha(hitsLabel, (float) alpha);
        } else if (scrollY > (bannerHeight / 3) * 2) {
            ViewHelper.setAlpha(hitsLabel, 1);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
