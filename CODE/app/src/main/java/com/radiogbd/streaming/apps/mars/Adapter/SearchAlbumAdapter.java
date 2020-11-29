package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Fragment.SearchAlbum;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.ExclusiveModel;
import com.radiogbd.streaming.apps.mars.Model.SearchAlbumModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Hp on 12/5/2017.
 */

public class SearchAlbumAdapter extends BaseAdapter {

    Context context;
    List<SearchAlbumModel> searchAlbums;
    Utility utility;

    public SearchAlbumAdapter(Context context, List<SearchAlbumModel> searchAlbums){
        this.context = context;
        utility = new Utility(this.context);
        this.searchAlbums = searchAlbums;
    }

    @Override
    public int getCount() {
        return searchAlbums.size();
    }

    @Override
    public Object getItem(int position) {
        return searchAlbums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return searchAlbums.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.album_layout,null);
            }
            LinearLayout albumCover = (LinearLayout) convertView.findViewById(R.id.albumCover);
            ImageView albumImage = (ImageView)convertView.findViewById(R.id.albumImage);
            TextView albumTitle = (TextView)convertView.findViewById(R.id.albumTitle);
            utility.setFont(albumTitle);
            albumTitle.setTextSize(16);
            HashMap<String, Integer> screenResolution = utility.getScreenRes();
            int screenWidth = (screenResolution.get(KeyWord.SCREEN_WIDTH)/2)-20;
            int screenHeight = screenWidth;
            ViewGroup.LayoutParams params = albumImage.getLayoutParams();
            params.width = screenWidth;
            params.height = screenHeight;
            albumImage.setLayoutParams(params);
            Picasso.with(context)
                    .load(context.getString(R.string.image_url)+ searchAlbums.get(position).getAlbumImage())
                    .error(android.R.drawable.screen_background_light_transparent)
                    .placeholder(android.R.drawable.screen_background_light_transparent)
                    .into(albumImage);
//            Ion.with(context)
//                    .load(context.getString(R.string.image_url)+searchAlbums.get(position).getAlbumImage())
//                    .withBitmap()
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.broken_image)
//                    .intoImageView(albumImage);
            /*Glide.with(context)
                    .load(context.getString(R.string.image_url)+searchAlbums.get(position).getAlbumImage())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                            ?Html.fromHtml(searchAlbums.get(position).getTitle_bn())
                            :searchAlbums.get(position).getTitle()
            );
            albumCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlbumModel albumModel = new AlbumModel();
                    albumModel.setId(searchAlbums.get(position).getId());
                    albumModel.setTitle(searchAlbums.get(position).getTitle());
                    albumModel.setTitle_bn(searchAlbums.get(position).getTitle_bn());
                    albumModel.setThumbnail(searchAlbums.get(position).getAlbumImage());
                    albumModel.setRelease_date(searchAlbums.get(position).getRelease_date());
                    Intent intent = new Intent(context,PlayList.class);
                    intent.putExtra("Album", albumModel);
                    context.startActivity(intent);
                }
            });

        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return convertView;
    }
}
