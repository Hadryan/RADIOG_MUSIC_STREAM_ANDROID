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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Hp on 12/14/2017.
 */

public class Music extends Fragment {

    Context context;
    Utility utility;
    AlbumModel albumModel;
    RelativeLayout albumBanner;
    ImageView albumCover;

    public Music() {
    }

    @SuppressLint("ValidFragment")
    public Music(Context context, AlbumModel albumModel) {
        this.context = context;
        this.utility = new Utility(this.context);
        this.albumModel = albumModel;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_layout, null);
        albumBanner = (RelativeLayout) view.findViewById(R.id.albumBanner);
        albumCover = (ImageView) view.findViewById(R.id.albumCover);
        Picasso.with(context)
                .load("http://radiogbd.com"+ albumModel.getThumbnail())
                .error(R.drawable.app_icon)
                .placeholder(R.drawable.app_icon)
                .into(albumCover);
        return view;
    }
}
