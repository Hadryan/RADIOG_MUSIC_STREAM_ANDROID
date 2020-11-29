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

import com.radiogbd.streaming.apps.mars.Adapter.CategoryAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.GenreModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Category extends Fragment {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    List<GenreModel> genreModels = null;
    TextView categoryText;
    ListView categoryList;
    CategoryAdapter categoryAdapter;
    SubscriptionBox subscriptionBox;

    public Category() {
    }

    @SuppressLint("ValidFragment")
    public Category(Context context) {
        this.context = context;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_layout, null);
        categoryText = (TextView) view.findViewById(R.id.categoryText);
        categoryList = (ListView) view.findViewById(R.id.categoryList);
        getAllCategory();
        return view;
    }

    private void getAllCategory() {
        if (utility.isNetworkAvailable()) {
            try {
                utility.showProgress(false);
                Call<List<GenreModel>> call = apiInterface.getAllCategory(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
                call.enqueue(new Callback<List<GenreModel>>() {
                    @Override
                    public void onResponse(Call<List<GenreModel>> call, Response<List<GenreModel>> response) {
                        utility.hideProgress();
                        if (response.isSuccessful() && response.code() == 200) {
                            try {
                                genreModels = response.body();
                                /*genreModels.add(0, new GenreModel(0, "All Album", "", "0", R.drawable.allalbum));
                                genreModels.add(1, new GenreModel(0, "All Artist", "", "0", R.drawable.allartist));*/
                                genreModels.add(0, new GenreModel(0, "All Album", "", "all_album", R.drawable.allalbum));
                                genreModels.add(1, new GenreModel(0, "All Artist", "", "all_artist", R.drawable.allartist));
                                for (int i = 0; i < genreModels.size(); i++) {
                                    GenreModel genreModel = genreModels.get(i);
                                    genreModel.setBanner(utility.getResourceId(genreModel.getTitle()));
                                }
                                utility.logger("Highlight List Size = " + genreModels.size());
                                if (genreModels.size() > 0) {
                                    categoryText.setVisibility(View.GONE);
                                    categoryList.setVisibility(View.VISIBLE);
                                    categoryAdapter = new CategoryAdapter(context, genreModels);
                                    categoryAdapter.notifyDataSetChanged();
                                    categoryList.setAdapter(categoryAdapter);
                                }
                            } catch (Exception ex) {
                                utility.logger(ex.toString());
                            }
                        } else {
                            utility.showToast("Response Code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<GenreModel>> call, Throwable t) {
                        utility.hideProgress();
                        utility.logger(t.toString());
                        utility.showToast(context.getString(R.string.http_error));
                    }
                });
            } catch (Exception e) {
                utility.logger(e.toString());
            }
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }


}
