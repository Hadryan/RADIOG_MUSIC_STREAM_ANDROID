package com.radiogbd.streaming.apps.mars.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.radiogbd.streaming.apps.mars.Fragment.AllAlbum;
import com.radiogbd.streaming.apps.mars.Fragment.AllArtist;
import com.radiogbd.streaming.apps.mars.Fragment.CategoryAlbum;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.GenreModel;
import com.radiogbd.streaming.apps.mars.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Hp on 12/6/2017.
 */

public class CategoryAdapter extends BaseAdapter {

    Context context;
    Utility utility;
    List<GenreModel> genreModels;
    AlbumAdapter albumAdapter;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FragmentActivity fragmentActivity;

    public CategoryAdapter(Context context, List<GenreModel> genreModels) {
        this.context = context;
        utility = new Utility(this.context);
        this.genreModels = genreModels;
        fragmentActivity = (FragmentActivity) context;
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
    }

    @Override
    public int getCount() {
        return genreModels.size();
    }

    @Override
    public Object getItem(int position) {
        return genreModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return genreModels.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_child_layout, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.categoryBannerImage);
            //imageView.setImageResource(genreModels.get(position).getBanner());
            if (position == 0 || position == 1) {
                Glide.with(context)
                        .load(context.getString(R.string.cat_image_url) + genreModels.get(position).getTotal() + ".jpg")
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.rg)
                        .placeholder(R.drawable.loadin_g)
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                                super.onResourceReady(drawable, anim);
                            }
                        });
            } else {
                Glide.with(context)
                        .load(context.getString(R.string.cat_image_url) + genreModels.get(position).getId() + ".jpg")
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.rg)
                        .placeholder(R.drawable.loadin_g)
                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            public void onResourceReady(Bitmap drawable, GlideAnimation anim) {
                                super.onResourceReady(drawable, anim);
                            }
                        });
            }
            HashMap<String, Integer> screenResolution = utility.getScreenRes();
            int imageWidth = screenResolution.get(KeyWord.SCREEN_WIDTH);
            int imageHeight = imageWidth / 3;
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = imageWidth;
            params.height = imageHeight;
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (genreModels.get(position).getTitle().equals("All Album")) {
                        AllAlbum allAlbum = new AllAlbum(context);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.containerView, allAlbum, "All Album");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if (genreModels.get(position).getTitle().equals("All Artist")) {
                        AllArtist allArtist = new AllArtist(context);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.containerView, allArtist, "All Artist");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        CategoryAlbum categoryAlbum = new CategoryAlbum(context, genreModels.get(position));
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.containerView, categoryAlbum, "Category Album");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            });
        } catch (Exception e) {
            utility.logger(e.toString());
        }
        return convertView;
    }
}
