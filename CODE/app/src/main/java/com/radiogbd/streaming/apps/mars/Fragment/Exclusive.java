package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.ExclusiveAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.EndlessScrollListener;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.ExclusiveModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Exclusive extends Fragment {

    Context context;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    List<ExclusiveModel> exclusiveModels = null;
    TextView exclusiveNoData;
    GridView exclusiveHitLists;
    ExclusiveAdapter exclusiveAdapter;
    SubscriptionBox subscriptionBox;

    public Exclusive(){}

    @SuppressLint("ValidFragment")
    public  Exclusive(Context context){
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        exclusiveModels = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exclusive_layout,null);
        exclusiveNoData = (TextView)view.findViewById(R.id.exclusiveNoData);
        exclusiveHitLists = (GridView)view.findViewById(R.id.exclusiveHitLists);
        getHitLists(0);
        exclusiveHitLists.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: "+(page-1)+", Total Count: "+totalItemsCount);
                if(totalItemsCount%20==0) {
                    getHitLists(page - 1);
                }
                return true;
            }
        });
        return view;
    }

    private void getHitLists(int page){
        if(utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<ExclusiveModel>> call = apiInterface.getHighlightList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), String.valueOf(page));
                call.enqueue(new Callback<List<ExclusiveModel>>() {
                    @Override
                    public void onResponse(Call<List<ExclusiveModel>> call, Response<List<ExclusiveModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                List<ExclusiveModel> models = response.body();
                                exclusiveModels.addAll(models);
                                utility.logger("Highlight List Size = " + exclusiveModels.size());
                                if (exclusiveModels.size() > 0) {
                                    exclusiveNoData.setVisibility(View.GONE);
                                    exclusiveHitLists.setVisibility(View.VISIBLE);
                                    exclusiveAdapter = new ExclusiveAdapter(context, exclusiveModels);
                                    exclusiveAdapter.notifyDataSetChanged();
                                    exclusiveHitLists.setAdapter(exclusiveAdapter);
                                    exclusiveHitLists.setSelection(exclusiveModels.size() > 20 ? exclusiveModels.size() - models.size() - 4 : 0);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ExclusiveModel>> call, Throwable t) {
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
}
