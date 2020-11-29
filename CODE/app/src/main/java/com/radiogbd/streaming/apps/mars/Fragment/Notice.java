package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.NoticeAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.OfflineAdapter;
import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.NoticeModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Notice extends Fragment {

    Context context;
    Utility utility;
    ListView listView;
    TextView tvNoData;
    NoticeAdapter adapter;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    SubscriptionBox subscriptionBox;

    public Notice(){}

    @SuppressLint("ValidFragment")
    public Notice(Context context){
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice,null);
        listView = (ListView)view.findViewById(R.id.listview);
        tvNoData = (TextView)view.findViewById(R.id.tv_no_data);
        utility.setFonts(new View[]{tvNoData});
        getNotices();
        return view;
    }

    private void getNotices(){
        utility.showProgress(true);
        Call<List<NoticeModel>> call = apiInterface.getNotice(utility.getAuthorization(), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<NoticeModel>>() {
            @Override
            public void onResponse(Call<List<NoticeModel>> call, Response<List<NoticeModel>> response) {
                utility.hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    List<NoticeModel> noticeModels = response.body();
                    if(noticeModels.size()>0){
                        utility.hideAndShowView(new View[]{listView, tvNoData}, listView);
                        adapter = new NoticeAdapter(context, noticeModels);
                        listView.setAdapter(adapter);
                    }
                    else{
                        utility.hideAndShowView(new View[]{listView, tvNoData}, tvNoData);
                    }
                }
                else{
                    utility.hideAndShowView(new View[]{listView, tvNoData}, tvNoData);
                    tvNoData.setText(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NoticeModel>> call, Throwable t) {

            }
        });
    }
}
