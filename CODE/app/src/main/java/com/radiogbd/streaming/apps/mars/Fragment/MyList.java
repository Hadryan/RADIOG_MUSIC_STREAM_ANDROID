package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.radiogbd.streaming.apps.mars.Adapter.MyListAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.MyListModel;
import com.radiogbd.streaming.apps.mars.Model.SelectedModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class MyList extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    TextView myListTitle;
    LinearLayout myListLabel;
    ObservableListView myListLists;
    ImageView myListBanner;
    List<MyListModel> myListModels = null;
    MyListAdapter myListAdapter;
    int bannerWidth;
    int bannerHeight;
    PlayInterface playInterface;
    SubscriptionBox subscriptionBox;

    public MyList(){}

    @SuppressLint("ValidFragment")
    public  MyList(Context context, PlayInterface playInterface){
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mylist_layout,null);
        myListTitle = (TextView) view.findViewById(R.id.myListTitle);
        myListLists = (ObservableListView) view.findViewById(R.id.myListLists);
        myListBanner = (ImageView) view.findViewById(R.id.myListBanner);
        myListLabel = (LinearLayout) view.findViewById(R.id.myListLabel);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = (bannerWidth / 8) * 5;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        myListBanner.setLayoutParams(params);
        RelativeLayout.LayoutParams labelParam = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight / 3);
        myListLabel.setLayoutParams(labelParam);
        myListLists.setScrollViewCallbacks(this);
        getAllMyList();
        return view;
    }

    private void getAllMyList() {
        if(utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<MyListModel>> call = apiInterface.getMyList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
                call.enqueue(new Callback<List<MyListModel>>() {
                    @Override
                    public void onResponse(Call<List<MyListModel>> call, Response<List<MyListModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                myListModels = response.body();
                                utility.logger("Hits List Size = " + myListModels.size());
                                if (myListModels.size() > 0) {
                                    for(int i=0; i<context.getResources().getInteger(R.integer.list_min_data_count); i++){
                                        myListModels.add(new MyListModel(0));
                                    }
                                    myListAdapter = new MyListAdapter(context, myListModels, playInterface);
                                    myListAdapter.notifyDataSetChanged();
                                    myListLists.setAdapter(myListAdapter);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyListModel>> call, Throwable t) {
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
        ViewHelper.setTranslationY(myListBanner, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(myListLists, Math.max(0, -scrollY + bannerHeight));
        if (scrollY < (bannerHeight / 2)) {
            ViewHelper.setAlpha(myListLabel, 0);
        } else if (scrollY >= (bannerHeight / 2) && scrollY <= (bannerHeight / 3) * 2) {
            int alphaValue = ((bannerHeight / 3) * 2) - (bannerHeight / 2);
            int scaleValue = ((bannerHeight / 3) * 2) - scrollY;
            String value = String.format("%.1f", 1.0 - (float) scaleValue / alphaValue);
            double alpha = Double.parseDouble(value);
            ViewHelper.setAlpha(myListLabel, (float) alpha);
        } else if (scrollY > (bannerHeight / 3) * 2) {
            ViewHelper.setAlpha(myListLabel, 1);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
