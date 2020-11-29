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
import android.widget.ListView;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.SongAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

/**
 * Created by fojlesaikat on 12/28/17.
 */

public class SearchTracks extends Fragment {

    Context context;
    Utility utility;
    List<SongModel> songModels;
    TextView title;
    ListView listView;
    String keyword;
    SongAdapter adapter;
    PlayInterface playInterface;

    public  SearchTracks(){}

    @SuppressLint("ValidFragment")
    public SearchTracks(Context context, List<SongModel> songModels, String keyword, PlayInterface playInterface){
        this.context = context;
        utility = new Utility(this.context);
        this.songModels = songModels;
        this.keyword = keyword;
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_track_layout, null);
        title = (TextView) view.findViewById(R.id.title);
        listView = (ListView) view.findViewById(R.id.listview);
        title.setText(utility.getLangauge().equals("bn")? Html.fromHtml(context.getString(R.string.search_result))+" "+keyword:"Search Result for "+keyword+":");
        utility.setFont(title);
        title.setTextSize(12);
        adapter = new SongAdapter(context, songModels, playInterface, KeyWord.FRAGMENT);
        listView.setAdapter(adapter);
        return view;
    }
}
