package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.koushikdutta.ion.Ion;
import com.radiogbd.streaming.apps.mars.Activity.Main;
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Fragment.ArtistAlbum;
import com.radiogbd.streaming.apps.mars.Fragment.Music;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.ExclusiveModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Hp on 12/5/2017.
 */

public class AlbumAdapter extends BaseAdapter {

    Context context;
    List<AlbumModel> albumModels;
    Utility utility;

    public AlbumAdapter(Context context, List<AlbumModel> albumModels){
        this.context = context;
        utility = new Utility(this.context);
        this.albumModels = albumModels;
    }

    @Override
    public int getCount() {
        return albumModels.size();
    }

    @Override
    public Object getItem(int position) {
        return albumModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return albumModels.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            AlbumModel albumModel = albumModels.get(position);
            if(albumModel.getId()!=0) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.album_layout, null);
                }
                LinearLayout albumCover = (LinearLayout) convertView.findViewById(R.id.albumCover);
                ImageView albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
                TextView albumTitle = (TextView) convertView.findViewById(R.id.albumTitle);
                utility.setFont(albumTitle);
                albumTitle.setTextSize(16);
                HashMap<String, Integer> screenResolution = utility.getScreenRes();
                int screenWidth = (screenResolution.get(KeyWord.SCREEN_WIDTH) / 2) - 20;
                int screenHeight = screenWidth;
                ViewGroup.LayoutParams params = albumImage.getLayoutParams();
                params.width = screenWidth;
                params.height = screenHeight;
                new ViewGroup.LayoutParams(screenWidth, screenHeight);
                albumImage.setLayoutParams(params);
                Picasso.with(context)
                        .load(context.getString(R.string.image_url) + albumModels.get(position).getThumbnail())
                        .error(android.R.drawable.screen_background_light_transparent)
                        .placeholder(android.R.drawable.screen_background_light_transparent)
                        .into(albumImage);
//            Ion.with(context)
//                    .load(context.getString(R.string.image_url)+albumModels.get(position).getThumbnail())
//                    .withBitmap()
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.broken_image)
//                    .intoImageView(albumImage);
            /*Glide.with(context)
                    .load(context.getString(R.string.image_url)+albumModels.get(position).getThumbnail())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(100,100)
                    .into(new BitmapImageViewTarget(albumImage){
                        @Override
                        public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                            loadingImage.setVisibility(View.GONE);
                        }
                    });*/
                String album = "";
                albumTitle.setText(
                        utility.getLangauge().equals("bn")
                                ? Html.fromHtml(albumModels.get(position).getTitle_bn())
                                : albumModels.get(position).getTitle()
                );
                albumCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PlayList.class);
                        intent.putExtra("Album", albumModels.get(position));
                        context.startActivity(intent);
                    }
                });
            }
            else{
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.empty_layout, null);
                }
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return albumModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
