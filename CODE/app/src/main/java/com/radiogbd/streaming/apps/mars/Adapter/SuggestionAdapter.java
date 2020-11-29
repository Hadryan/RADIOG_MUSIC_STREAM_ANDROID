package com.radiogbd.streaming.apps.mars.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SuggestionModel;
import com.radiogbd.streaming.apps.mars.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fojlesaikat on 12/29/17.
 */

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    Context context;
    Utility utility;
    List<SuggestionModel> suggestionModels;

    public SuggestionAdapter(Context context, List<SuggestionModel> suggestionModels){
        this.context = context;
        utility = new Utility(this.context);
        this.suggestionModels = suggestionModels;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_layout, parent, false);
        SuggestionAdapter.ViewHolder viewHolder = new SuggestionAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext())
                .load(context.getString(R.string.image_url)+suggestionModels.get(position).getHighres())
                .placeholder(context.getResources().getDrawable(R.drawable.loading))
                .error(context.getResources().getDrawable(R.drawable.broken_image))
                .into(holder.image);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlbumModel albumModel = new AlbumModel();
                    albumModel.setId(suggestionModels.get(position).getId());
                    albumModel.setTitle(suggestionModels.get(position).getTitle());
                    albumModel.setTitle_bn(suggestionModels.get(position).getTitle_bn());
                    albumModel.setThumbnail(suggestionModels.get(position).getHighres());
                    albumModel.setRelease_date(suggestionModels.get(position).getRelease_date());
                    Intent intent = new Intent(context,PlayList.class);
                    intent.putExtra("Album", albumModel);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
                catch (Exception ex){
                    utility.logger(ex.toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestionModels.size();
    }
}
