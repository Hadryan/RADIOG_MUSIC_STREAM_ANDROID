package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.AlbumRecycleAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.ArtistRecycleAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.SongAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SearchAlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SearchArtistModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 12/26/2017.
 */

public class Search extends Fragment {

    Context context;
    Utility utility;
    String keyword;
    TextView searchKeyword;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    List<SongModel> trackSong, lyricSong, tuneSong;
    List<SearchAlbumModel> albumSong;
    List<SearchArtistModel> artistSong;
    String search = "search";
    SongAdapter songAdapter;
    AlbumRecycleAdapter albumRecycleAdapter;
    ArtistRecycleAdapter artistRecycleAdapter;
    PlayInterface playInterface;
    ImageView trackLoader, lyricLoader, tuneLoader, albumLoader, artistLoader;
    LinearLayout trackData, lyricData, tuneData, albumData, artistData;
    ListView trackList, lyricList, tuneList;
    RecyclerView albumList, artistList;
    Button seeMoreTrack, seeMoreLyric, seeMoreTune, seeMoreAlbum, seeMoreArtist;
    TextView trackNoData, lyricNoData, tuneNoData, albumNoData, artistNoData;
    TextView searchTrack, searchAlbum, searchArtist, searchLyrics, searchTune;
    LinearLayout trackBody, albumBody, artistBody, lyricsBody, tuneBody;
    SubscriptionBox subscriptionBox;

    public Search() {
    }

    @SuppressLint("ValidFragment")
    public Search(Context context, String keyword, PlayInterface playInterface) {
        this.context = context;
        this.keyword = keyword;
        utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, null);
        searchKeyword = (TextView) view.findViewById(R.id.searchKeyword);
        utility.setFont(searchKeyword);
        searchKeyword.setTextSize(12);
        searchKeyword.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_result) + " " + keyword + ":" : "Search result for " + keyword + ":");
        trackLoader = (ImageView) view.findViewById(R.id.trackLoader);
        trackData = (LinearLayout) view.findViewById(R.id.trackData);
        trackList = (ListView) view.findViewById(R.id.trackList);
        seeMoreTrack = (Button) view.findViewById(R.id.seeMoreTracks);

        //Search Fragment Body
        trackBody = (LinearLayout) view.findViewById(R.id.searchTrackBody);
        albumBody = (LinearLayout) view.findViewById(R.id.searchAlbumBody);
        artistBody = (LinearLayout) view.findViewById(R.id.searchArtistBody);
        lyricsBody = (LinearLayout) view.findViewById(R.id.searchLyricsBody);
        tuneBody = (LinearLayout) view.findViewById(R.id.searchTuneBody);

        utility.setFont(seeMoreTrack);
        seeMoreTrack.setTextSize(14);
        trackNoData = (TextView) view.findViewById(R.id.trackNoData);
        utility.setFont(trackNoData);
        trackNoData.setTextSize(16);
        lyricLoader = (ImageView) view.findViewById(R.id.lyricLoader);
        lyricData = (LinearLayout) view.findViewById(R.id.lyricData);
        lyricList = (ListView) view.findViewById(R.id.lyricList);
        seeMoreLyric = (Button) view.findViewById(R.id.seeMoreLyrics);
        utility.setFont(seeMoreLyric);
        seeMoreLyric.setTextSize(14);
        lyricNoData = (TextView) view.findViewById(R.id.lyricNoData);
        utility.setFont(lyricNoData);
        lyricNoData.setTextSize(16);
        tuneLoader = (ImageView) view.findViewById(R.id.tuneLoader);
        tuneData = (LinearLayout) view.findViewById(R.id.tuneData);
        tuneList = (ListView) view.findViewById(R.id.tuneList);
        seeMoreTune = (Button) view.findViewById(R.id.seeMoreTunes);
        utility.setFont(seeMoreTune);
        seeMoreTune.setTextSize(14);
        tuneNoData = (TextView) view.findViewById(R.id.tuneNoData);
        utility.setFont(tuneNoData);
        tuneNoData.setTextSize(16);
        albumLoader = (ImageView) view.findViewById(R.id.albumLoader);
        albumData = (LinearLayout) view.findViewById(R.id.albumData);
        albumList = (RecyclerView) view.findViewById(R.id.albumList);
        seeMoreAlbum = (Button) view.findViewById(R.id.seeMoreAlbums);
        utility.setFont(seeMoreAlbum);
        seeMoreAlbum.setTextSize(14);
        albumNoData = (TextView) view.findViewById(R.id.albumNoData);
        utility.setFont(albumNoData);
        albumNoData.setTextSize(16);
        artistLoader = (ImageView) view.findViewById(R.id.artistLoader);
        artistData = (LinearLayout) view.findViewById(R.id.artistData);
        artistList = (RecyclerView) view.findViewById(R.id.artistList);
        seeMoreArtist = (Button) view.findViewById(R.id.seeMoreArtists);
        utility.setFont(seeMoreArtist);
        seeMoreArtist.setTextSize(14);
        artistNoData = (TextView) view.findViewById(R.id.artistNoData);
        utility.setFont(artistNoData);
        artistNoData.setTextSize(16);
        searchTrack = (TextView) view.findViewById(R.id.searchTrack);
        utility.setFont(searchTrack);
        searchTrack.setTextSize(16);
        searchAlbum = (TextView) view.findViewById(R.id.searchAlbum);
        utility.setFont(searchAlbum);
        searchAlbum.setTextSize(16);
        searchArtist = (TextView) view.findViewById(R.id.searchArtist);
        utility.setFont(searchArtist);
        searchArtist.setTextSize(16);
        searchLyrics = (TextView) view.findViewById(R.id.searchLyrics);
        utility.setFont(searchLyrics);
        searchLyrics.setTextSize(16);
        searchTune = (TextView) view.findViewById(R.id.searchTune);
        utility.setFont(searchTune);
        searchTune.setTextSize(16);
        searchTrack.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_track) : "Tracks");
        searchAlbum.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_album) : "Albums");
        searchArtist.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_artist) : "Artists");
        searchLyrics.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_lyrics) : "Lyrics");
        searchTune.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.search_tune) : "Tunes");
        LinearLayoutManager albumManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        albumList.setLayoutManager(albumManager);
        LinearLayoutManager artistManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        artistList.setLayoutManager(artistManager);
        getTrackList(1);
        getAlbumList(2);
        getArtistList(3);
        getLyricList(4);
        getTuneList(5);
        seeMoreTrack.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.see_more) : "See More");
//        if (utility.getLangauge().equals("en")) {
//            seeMoreTrack.setPadding(0, 10, 0, 0);
//            seeMoreArtist.setPadding(0, 10, 0, 0);
//            seeMoreAlbum.setPadding(0, 10, 0, 0);
//            seeMoreTune.setPadding(0, 10, 0, 0);
//            seeMoreLyric.setPadding(0, 10, 0, 0);
//        }
        seeMoreTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTracks searchTracks = new SearchTracks(context, trackSong, keyword, playInterface);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, searchTracks, "searchTrack");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        seeMoreAlbum.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.see_more) : "See More");
        seeMoreAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchAlbum searchTracks = new SearchAlbum(context, albumSong, keyword, playInterface);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, searchTracks, "searchTrack");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        seeMoreArtist.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.see_more) : "See More");
        seeMoreArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchArtist searchTracks = new SearchArtist(context, artistSong, keyword, playInterface);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, searchTracks, "searchTrack");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        seeMoreLyric.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.see_more) : "See More");
        seeMoreLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTracks searchTracks = new SearchTracks(context, lyricSong, keyword, playInterface);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, searchTracks, "searchTrack");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        seeMoreTune.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.see_more) : "See More");
        seeMoreTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTracks searchTracks = new SearchTracks(context, tuneSong, keyword, playInterface);
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.containerView, searchTracks, "searchTrack");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        trackNoData.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no_data) : "No Data");
        albumNoData.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no_data) : "No Data");
        artistNoData.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no_data) : "No Data");
        lyricNoData.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no_data) : "No Data");
        tuneNoData.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no_data) : "No Data");
        return view;
    }

    private List<SongModel> getModifiedSong(List<SongModel> songModels) {
        List<SongModel> songs = new ArrayList<>();
        for (int i = 0; i < 4 && i < songModels.size(); i++) {
            SongModel songModel = new SongModel();
            songModel.setId(songModels.get(i).getId());
            songModel.setAlbumImage(songModels.get(i).getAlbumImage());
            songModel.setAlbum(songModels.get(i).getAlbum());
            songModel.setTokenFav(songModels.get(i).getTokenFav());
            songModel.setAlbum_bn(songModels.get(i).getAlbum_bn());
            songModel.setArtist(songModels.get(i).getArtist());
            songModel.setArtist_bn(songModels.get(i).getArtist_bn());
            songModel.setLink(songModels.get(i).getLink());
            songModel.setLyrics(songModels.get(i).getLyrics());
            songModel.setLyrics_bn(songModels.get(i).getLyrics_bn());
            songModel.setTitle(songModels.get(i).getTitle());
            songModel.setTitle_bn(songModels.get(i).getTitle_bn());
            songModel.setTune(songModels.get(i).getTune());
            songModel.setTune_bn(songModels.get(i).getTune_bn());
            songModel.setRelease_date(songModels.get(i).getRelease_date());
            songModel.setPremium(songModels.get(i).getPremium());
            songs.add(songModel);
        }
        return songs;
    }

    private void getTrackList(int id) {
        utility.hideAndShowView(new View[]{trackLoader, trackData, trackNoData}, trackLoader);
        Call<List<SongModel>> call = apiInterface.getTrackListBySearch(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), search + "-" + id + "-" + keyword);
        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        utility.hideAndShowView(new View[]{trackLoader, trackData, trackNoData}, trackData);
                        int size = 0;
                        trackSong = response.body();
                        if (trackSong.size() == 0) {
                            trackBody.setVisibility(View.GONE);
                        } else {
                            if (trackSong.size() > 4) {
                                size = 5;
//                            for(int i=4; i<trackSong.size();){
//                                trackSong.remove(4);
//                            }
                            } else {
                                size = trackSong.size() + 1;
                                seeMoreTrack.setVisibility(View.GONE);
                            }

                            songAdapter = new SongAdapter(context, getModifiedSong(trackSong), playInterface, KeyWord.FRAGMENT);
                            trackList.setAdapter(songAdapter);
                            float density = context.getResources().getDisplayMetrics().density;
                            int height = (int) (size * density * 50);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                            trackList.setLayoutParams(params);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.hideAndShowView(new View[]{trackLoader, trackData, trackNoData}, trackNoData);
                        trackBody.setVisibility(View.GONE);
                    }
                } else {
                    //utility.showToast("Response Code: "+response.code());
                    //utility.hideAndShowView(new View[]{trackLoader, trackData, trackNoData}, trackNoData);
                    trackBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                //utility.hideAndShowView(new View[]{trackLoader, trackData, trackNoData}, trackNoData);
                trackBody.setVisibility(View.GONE);
            }
        });
    }

    private void getAlbumList(int id) {
        utility.hideAndShowView(new View[]{albumLoader, albumData, albumNoData}, albumLoader);
        Call<List<SearchAlbumModel>> call = apiInterface.getAlbumListBySearch(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), search + "-" + id + "-" + keyword);
        call.enqueue(new Callback<List<SearchAlbumModel>>() {
            @Override
            public void onResponse(Call<List<SearchAlbumModel>> call, Response<List<SearchAlbumModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        utility.hideAndShowView(new View[]{albumLoader, albumData, albumNoData}, albumData);
                        //int size = 0;
                        albumSong = response.body();
                        if (albumSong.size() == 0) {
                            albumBody.setVisibility(View.GONE);
                        } else {
                            if (albumSong.size() > 4) {
                                //size = 5;
//                            for(int i=4; i<albumSong.size();){
//                                albumSong.remove(4);
//                            }
                            } else {
                                //size = albumSong.size()+1;
                                seeMoreAlbum.setVisibility(View.GONE);
                            }
                            List<SearchAlbumModel> searchAlbumModels = new ArrayList<>();
                            for (int i = 0; i < 4 && i < albumSong.size(); i++) {
                                SearchAlbumModel searchAlbumModel = new SearchAlbumModel();
                                searchAlbumModel.setId(albumSong.get(i).getId());
                                searchAlbumModel.setTitle(albumSong.get(i).getTitle());
                                searchAlbumModel.setTitle_bn(albumSong.get(i).getTitle_bn());
                                searchAlbumModel.setAlbumImage(albumSong.get(i).getAlbumImage());
                                searchAlbumModel.setRelease_date(albumSong.get(i).getRelease_date());
                                searchAlbumModels.add(searchAlbumModel);
                            }
                            albumRecycleAdapter = new AlbumRecycleAdapter(context, searchAlbumModels);
                            albumList.setAdapter(albumRecycleAdapter);
//                        float density = context.getResources().getDisplayMetrics().density;
//                        int height = (int) (size * density * 50);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
//                        lyricList.setLayoutParams(params);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.hideAndShowView(new View[]{albumLoader, albumData, albumNoData}, albumNoData);
                        albumBody.setVisibility(View.GONE);
                    }
                } else {
                    //utility.showToast("Response Code: "+response.code());
                    //utility.hideAndShowView(new View[]{albumLoader, albumData, albumNoData}, albumNoData);
                    albumBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<SearchAlbumModel>> call, Throwable t) {
                //utility.hideAndShowView(new View[]{albumLoader, albumData, albumNoData}, albumNoData);
                albumBody.setVisibility(View.GONE);
            }
        });
    }

    private void getArtistList(int id) {
        utility.hideAndShowView(new View[]{artistLoader, artistData, artistNoData}, artistLoader);
        Call<List<SearchArtistModel>> call = apiInterface.getArtistListBySearch(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), search + "-" + id + "-" + keyword);
        call.enqueue(new Callback<List<SearchArtistModel>>() {
            @Override
            public void onResponse(Call<List<SearchArtistModel>> call, Response<List<SearchArtistModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        utility.hideAndShowView(new View[]{artistLoader, artistData, artistNoData}, artistData);
                        //int size = 0;
                        artistSong = response.body();
                        if (artistSong.size() == 0) {
                            artistBody.setVisibility(View.GONE);
                        } else {
                            if (artistSong.size() > 4) {
                                //size = 5;
//                            for(int i=4; i<artistSong.size();){
//                                artistSong.remove(4);
//                            }
                            } else {
                                //size = artistSong.size()+1;
                                seeMoreArtist.setVisibility(View.GONE);
                            }
                            List<SearchArtistModel> searchArtistModels = new ArrayList<>();
                            for (int i = 0; i < 4 && i < artistSong.size(); i++) {
                                SearchArtistModel searchArtistModel = new SearchArtistModel();
                                searchArtistModel.setId(artistSong.get(i).getId());
                                searchArtistModel.setTitle(artistSong.get(i).getTitle());
                                searchArtistModel.setTitle_bn(artistSong.get(i).getTitle_bn());
                                searchArtistModel.setAlbumImage(artistSong.get(i).getAlbumImage());
                                searchArtistModels.add(searchArtistModel);
                            }
                            artistRecycleAdapter = new ArtistRecycleAdapter(context, searchArtistModels);
                            artistList.setAdapter(artistRecycleAdapter);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.hideAndShowView(new View[]{artistLoader, artistData, artistNoData}, artistNoData);
                        artistBody.setVisibility(View.GONE);
                    }
                } else {
                    //utility.showToast("Response Code: "+response.code());
                    //utility.hideAndShowView(new View[]{artistLoader, artistData, artistNoData}, artistNoData);
                    artistBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<SearchArtistModel>> call, Throwable t) {
                //utility.hideAndShowView(new View[]{artistLoader, artistData, artistNoData}, artistNoData);
                artistBody.setVisibility(View.GONE);
            }
        });
    }

    private void getLyricList(int id) {
        utility.hideAndShowView(new View[]{lyricLoader, lyricData, lyricNoData}, lyricLoader);
        Call<List<SongModel>> call = apiInterface.getTrackListBySearch(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), search + "-" + id + "-" + keyword);
        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        utility.hideAndShowView(new View[]{lyricLoader, lyricData, lyricNoData}, lyricData);
                        int size = 0;
                        lyricSong = response.body();
                        if (lyricSong.size() == 0) {
                            lyricsBody.setVisibility(View.GONE);
                        } else {
                            if (lyricSong.size() > 4) {
                                size = 5;
//                            for(int i=4; i<lyricSong.size();){
//                                lyricSong.remove(4);
//                            }
                            } else {
                                size = lyricSong.size() + 1;
                                seeMoreLyric.setVisibility(View.GONE);
                            }
                            songAdapter = new SongAdapter(context, getModifiedSong(lyricSong), playInterface, KeyWord.FRAGMENT);
                            lyricList.setAdapter(songAdapter);
                            float density = context.getResources().getDisplayMetrics().density;
                            int height = (int) (size * density * 50);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                            lyricList.setLayoutParams(params);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.hideAndShowView(new View[]{lyricLoader, lyricData, lyricNoData}, lyricNoData);
                        lyricsBody.setVisibility(View.GONE);
                    }
                } else {
                    //utility.showToast("Response Code: "+response.code());
                    //utility.hideAndShowView(new View[]{lyricLoader, lyricData, lyricNoData}, lyricNoData);
                    lyricsBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                //utility.hideAndShowView(new View[]{lyricLoader, lyricData, lyricNoData}, lyricNoData);
                lyricsBody.setVisibility(View.GONE);
            }
        });
    }

    private void getTuneList(int id) {
        utility.hideAndShowView(new View[]{tuneLoader, tuneData, tuneNoData}, tuneLoader);
        Call<List<SongModel>> call = apiInterface.getTrackListBySearch(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), search + "-" + id + "-" + keyword);
        call.enqueue(new Callback<List<SongModel>>() {
            @Override
            public void onResponse(Call<List<SongModel>> call, Response<List<SongModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        utility.hideAndShowView(new View[]{tuneLoader, tuneData, tuneNoData}, tuneData);
                        int size = 0;
                        tuneSong = response.body();
                        if (tuneSong.size() == 0) {
                            tuneBody.setVisibility(View.GONE);
                        } else {
                            if (tuneSong.size() > 4) {
                                size = 5;
//                            for(int i=4; i<tuneSong.size();){
//                                tuneSong.remove(4);
//                            }
                            } else {
                                size = tuneSong.size() + 1;
                                seeMoreTune.setVisibility(View.GONE);
                            }
                            songAdapter = new SongAdapter(context, getModifiedSong(tuneSong), playInterface, KeyWord.FRAGMENT);
                            tuneList.setAdapter(songAdapter);
                            float density = context.getResources().getDisplayMetrics().density;
                            int height = (int) (size * density * 50);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                            tuneList.setLayoutParams(params);
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                        //utility.hideAndShowView(new View[]{tuneLoader, tuneData, tuneNoData}, tuneNoData);
                        tuneBody.setVisibility(View.GONE);
                    }
                } else {
                    //utility.showToast("Response Code: "+response.code());
                    //utility.hideAndShowView(new View[]{tuneLoader, tuneData, tuneNoData}, tuneNoData);
                    tuneBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<SongModel>> call, Throwable t) {
                //utility.hideAndShowView(new View[]{tuneLoader, tuneData, tuneNoData}, tuneNoData);
                tuneBody.setVisibility(View.GONE);
            }
        });
    }
}
