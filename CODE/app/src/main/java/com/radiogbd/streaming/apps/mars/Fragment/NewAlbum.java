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
import com.radiogbd.streaming.apps.mars.Adapter.NewAlbumAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.EndlessScrollListener;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.ExclusiveModel;
import com.radiogbd.streaming.apps.mars.Model.NewAlbumModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class NewAlbum extends Fragment {

    Context context;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    Utility utility;
    List<NewAlbumModel> newAlbumModels = null;
    TextView newAlbumNoData;
    GridView newAlbumHitLists;
    NewAlbumAdapter newAlbumAdapter;
    SubscriptionBox subscriptionBox;

    public NewAlbum(){}

    @SuppressLint("ValidFragment")
    public  NewAlbum(Context context){
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        newAlbumModels = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newalbum_layout,null);
        newAlbumNoData = (TextView)view.findViewById(R.id.newAlbumNoData);
        newAlbumHitLists = (GridView)view.findViewById(R.id.newAlbumHitLists);
        getNewAlbumList(0);
        newAlbumHitLists.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                utility.logger("Page: "+(page-1)+", Total Count: "+totalItemsCount);
                if(totalItemsCount%20==0) {
                    getNewAlbumList(page - 1);
                }
                return true;
            }
        });
        return view;
    }

    private void getNewAlbumList(int path){
        if(utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<NewAlbumModel>> call = apiInterface.getRecentList(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), String.valueOf(path));
                call.enqueue(new Callback<List<NewAlbumModel>>() {
                    @Override
                    public void onResponse(Call<List<NewAlbumModel>> call, Response<List<NewAlbumModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                List<NewAlbumModel> models = response.body();
                                newAlbumModels.addAll(models);
                                utility.logger("Highlight List Size = " + newAlbumModels.size());
                                if (newAlbumModels.size() > 0) {
                                    newAlbumNoData.setVisibility(View.GONE);
                                    newAlbumHitLists.setVisibility(View.VISIBLE);
                                    newAlbumAdapter = new NewAlbumAdapter(context, newAlbumModels);
                                    newAlbumAdapter.notifyDataSetChanged();
                                    newAlbumHitLists.setAdapter(newAlbumAdapter);
                                    newAlbumHitLists.setSelection(newAlbumModels.size() > 20 ? newAlbumModels.size() - models.size() - 4 : 0);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NewAlbumModel>> call, Throwable t) {
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
