package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Fragment.ArtistAlbum;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.ArtistModel;
import com.radiogbd.streaming.apps.mars.Model.SearchAlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SearchArtistModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Hp on 12/28/2017.
 */

public class ArtistRecycleAdapter extends RecyclerView.Adapter<ArtistRecycleAdapter.ViewHolder> {

    List<SearchArtistModel> artistModels;
    Context context;
    View view;
    Utility utility;

    public ArtistRecycleAdapter(Context context, List<SearchArtistModel> artistModels){
        this.artistModels = artistModels;
        this.context = context;
        utility = new Utility(this.context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView image;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_album_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(utility.getLangauge().equals("bn")? Html.fromHtml(artistModels.get(position).getTitle_bn()):artistModels.get(position).getTitle());
        utility.setFont(holder.text);
        holder.text.setTextSize(16);
        Picasso.with(holder.itemView.getContext())
                .load(context.getString(R.string.image_url)+artistModels.get(position).getAlbumImage())
                .error(android.R.drawable.screen_background_light_transparent)
                .placeholder(android.R.drawable.screen_background_light_transparent)
                .into(holder.image);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                        CategorizedRecipe categorizedRecipe = new CategorizedRecipe(context, categories.get(position).getId());
//                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.containerView, categorizedRecipe);
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
//                    utility.showToast("I am clicked with "+artistModels.get(position).getTitle());
                    ArtistModel artistModel = new ArtistModel();
                    artistModel.setId(artistModels.get(position).getId());
                    artistModel.setTitle(artistModels.get(position).getTitle());
                    artistModel.setTitle_bn(artistModels.get(position).getTitle_bn());
                    artistModel.setThumbnail(artistModels.get(position).getAlbumImage());
                    ArtistAlbum categoryAlbum = new ArtistAlbum(context, artistModel);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.containerView, categoryAlbum, "artistAlbum");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                catch (Exception ex){
                    utility.logger(ex.toString());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return artistModels.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

}
