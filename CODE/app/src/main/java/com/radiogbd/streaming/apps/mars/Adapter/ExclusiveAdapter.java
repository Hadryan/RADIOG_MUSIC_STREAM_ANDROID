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
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.koushikdutta.ion.Ion;
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
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

public class ExclusiveAdapter extends BaseAdapter {

    Context context;
    List<ExclusiveModel> exclusiveModels;
    Utility utility;

    public ExclusiveAdapter(Context context, List<ExclusiveModel> exclusiveModels){
        this.context = context;
        utility = new Utility(this.context);
        this.exclusiveModels = exclusiveModels;
    }

    @Override
    public int getCount() {
        return exclusiveModels.size();
    }

    @Override
    public Object getItem(int position) {
        return exclusiveModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return exclusiveModels.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try{
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.album_layout,null);
            }
            LinearLayout albumCover = (LinearLayout) convertView.findViewById(R.id.albumCover);
            ImageView albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
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
                    .load(context.getString(R.string.image_url)+ exclusiveModels.get(position).getPath())
                    .error(android.R.drawable.screen_background_light_transparent)
                    .placeholder(android.R.drawable.screen_background_light_transparent)
                    .into(albumImage);
//            Ion.with(context)
//                    .load(context.getString(R.string.image_url)+exclusiveModels.get(position).getPath())
//                    .withBitmap()
//                    .placeholder(R.drawable.loading)
//                    .error(R.drawable.broken_image)
//                    .intoImageView(albumImage);
            /*Glide.with(context)
                    .load(context.getString(R.string.image_url)+exclusiveModels.get(position).getPath())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(100, 100)
                    .into(new BitmapImageViewTarget(albumImage){
                        @Override
                        public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                            super.onResourceReady(drawable, anim);
                            loadingImage.setVisibility(View.GONE);
                        }
                    });*/
//            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(albumImage);
//            Glide.with(context)
//                    .load(context.getString(R.string.image_url)+exclusiveModels.get(position).getPath())
//                    .placeholder(R.drawable.loadin_g)
//                    .into(imageViewTarget);
            String album = "";
            albumTitle.setText(
                    utility.getLangauge().equals("bn")
                            ?Html.fromHtml(exclusiveModels.get(position).getTxt_album_bn())
                            :exclusiveModels.get(position).getTxt_album()
            );
            albumCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlbumModel albumModel = new AlbumModel();
                    albumModel.setId(exclusiveModels.get(position).getId());
                    albumModel.setTitle(exclusiveModels.get(position).getTxt_album());
                    albumModel.setTitle_bn(exclusiveModels.get(position).getTxt_album_bn());
                    albumModel.setThumbnail(exclusiveModels.get(position).getPath());
                    albumModel.setRelease_date(exclusiveModels.get(position).getRelease_date());
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
