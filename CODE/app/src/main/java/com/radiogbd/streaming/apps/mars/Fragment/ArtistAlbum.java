package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.AlbumAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.ArtistAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.ArtistModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/14/2017.
 */

public class ArtistAlbum extends Fragment {

    Context context;
    List<AlbumModel> albumModelList;
    ArtistModel artistModel;
    TextView artistAlbumText;
    GridView artistAlbumList;
    ImageView artistImage;
    TextView artistName;
    LinearLayout parent;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    AlbumAdapter albumAdapter;
    SubscriptionBox subscriptionBox;

    public ArtistAlbum() {
    }

    @SuppressLint("ValidFragment")
    public ArtistAlbum(Context context, ArtistModel artistModel) {
        this.context = context;
        utility = new Utility(this.context);
        this.artistModel = artistModel;
        subscriptionBox = new SubscriptionBox(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_album_layout, null);
        artistAlbumText = (TextView) view.findViewById(R.id.artistAlbumText);
        artistAlbumList = (GridView) view.findViewById(R.id.artistAlbumList);
        artistImage = (ImageView) view.findViewById(R.id.artistImage);
        artistName = (TextView) view.findViewById(R.id.artistName);
        parent = (LinearLayout) view.findViewById(R.id.parent);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });
        Picasso.with(context)
                .load("http://radiogbd.com"+ artistModel.getThumbnail())
                .error(R.drawable.app_icon)
                .placeholder(R.drawable.app_icon)
                .into(artistImage);
        artistName.setText(utility.getLangauge().equals("bn")
                ? Html.fromHtml(artistModel.getTitle())
                :artistModel.getTitle());
        getArtistAlbumList();
        return view;
    }

    private void getArtistAlbumList() {
        if(utility.isNetworkAvailable()) {
            String album = "get_artist-" + artistModel.getId();
            utility.logger(album);
            utility.showProgress(false);
            Call<List<AlbumModel>> call = apiInterface.getAlbumByArtist(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), album);
            call.enqueue(new Callback<List<AlbumModel>>() {
                @Override
                public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            albumModelList = response.body();
                            utility.logger("Highlight List Size = " + albumModelList.size());
                            if (albumModelList.size() > 0) {
                                artistAlbumText.setVisibility(View.GONE);
                                artistAlbumList.setVisibility(View.VISIBLE);
                                albumAdapter = new AlbumAdapter(context, albumModelList);
                                albumAdapter.notifyDataSetChanged();
                                artistAlbumList.setAdapter(albumAdapter);
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
}
