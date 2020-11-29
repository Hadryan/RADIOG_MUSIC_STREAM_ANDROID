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
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.SearchAlbumAdapter;
import com.radiogbd.streaming.apps.mars.Adapter.SearchArtistAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SearchAlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SearchArtistModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

/**
 * Created by fojlesaikat on 12/28/17.
 */

public class SearchArtist extends Fragment {

    Context context;
    Utility utility;
    List<SearchArtistModel> searchArtistModels;
    TextView title;
    GridView gridView;
    String keyword;
    SearchArtistAdapter adapter;
    PlayInterface playInterface;

    public SearchArtist(){}

    @SuppressLint("ValidFragment")
    public SearchArtist(Context context, List<SearchArtistModel> searchArtistModels, String keyword, PlayInterface playInterface){
        this.context = context;
        utility = new Utility(this.context);
        this.searchArtistModels = searchArtistModels;
        this.keyword = keyword;
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_album_layout, null);
        title = (TextView) view.findViewById(R.id.title);
        gridView = (GridView) view.findViewById(R.id.gridview);
        title.setText(utility.getLangauge().equals("bn")? Html.fromHtml(context.getString(R.string.search_result))+" "+keyword:"Search Result for "+keyword+":");
        utility.setFont(title);
        title.setTextSize(12);
        gridView.setVisibility(View.VISIBLE);
        adapter = new SearchArtistAdapter(context, searchArtistModels);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        return view;
    }
}
