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
import com.radiogbd.streaming.apps.mars.Adapter.SelectedAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.HitsModel;
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

public class Selected extends Fragment implements ObservableScrollViewCallbacks {

    Context context;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    TextView selectedTitle;
    LinearLayout selectedLabel;
    ObservableListView selectedLists;
    ImageView selectedBanner;
    List<SelectedModel> selectedModels = null;
    SelectedAdapter selectedAdapter;
    int bannerWidth;
    int bannerHeight;
    PlayInterface playInterface;
    SubscriptionBox subscriptionBox;

    public Selected(){}

    @SuppressLint("ValidFragment")
    public Selected(Context context, PlayInterface playInterface) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.selected_layout, null);
        selectedTitle = (TextView) view.findViewById(R.id.selectedTitle);
        selectedLists = (ObservableListView) view.findViewById(R.id.selectedLists);
        selectedBanner = (ImageView) view.findViewById(R.id.selectedBanner);
        selectedLabel = (LinearLayout) view.findViewById(R.id.selectedLabel);
        HashMap<String, Integer> screenResolution = utility.getScreenRes();
        bannerWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
        bannerHeight = (bannerWidth / 8) * 5;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight);
        selectedBanner.setLayoutParams(params);
        RelativeLayout.LayoutParams labelParam = new RelativeLayout.LayoutParams(bannerWidth, bannerHeight / 3);
        selectedLabel.setLayoutParams(labelParam);
        selectedLists.setScrollViewCallbacks(this);
        getAllSelected();
        return view;
    }

    private void getAllSelected() {
        if(utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<SelectedModel>> call = apiInterface.getSelectedList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
                call.enqueue(new Callback<List<SelectedModel>>() {
                    @Override
                    public void onResponse(Call<List<SelectedModel>> call, Response<List<SelectedModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                selectedModels = response.body();
                                utility.logger("Hits List Size = " + selectedModels.size());
                                if (selectedModels.size() > 0) {
                                    for(int i=0; i<context.getResources().getInteger(R.integer.list_min_data_count); i++){
                                        selectedModels.add(new SelectedModel(0));
                                    }
                                    selectedAdapter = new SelectedAdapter(context, selectedModels, playInterface);
                                    selectedAdapter.notifyDataSetChanged();
                                    selectedLists.setAdapter(selectedAdapter);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SelectedModel>> call, Throwable t) {
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
        ViewHelper.setTranslationY(selectedBanner, Math.max(-bannerHeight, -scrollY));
        ViewHelper.setTranslationY(selectedLists, Math.max(0, -scrollY + bannerHeight));
        if (scrollY < (bannerHeight / 2)) {
            ViewHelper.setAlpha(selectedLabel, 0);
        } else if (scrollY >= (bannerHeight / 2) && scrollY <= (bannerHeight / 3) * 2) {
            int alphaValue = ((bannerHeight / 3) * 2) - (bannerHeight / 2);
            int scaleValue = ((bannerHeight / 3) * 2) - scrollY;
            String value = String.format("%.1f", 1.0 - (float) scaleValue / alphaValue);
            double alpha = Double.parseDouble(value);
            ViewHelper.setAlpha(selectedLabel, (float) alpha);
        } else if (scrollY > (bannerHeight / 3) * 2) {
            ViewHelper.setAlpha(selectedLabel, 1);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
