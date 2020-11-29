package com.radiogbd.streaming.apps.mars.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Fragment.Offline;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 1/11/2018.
 */

public class OfflineAdapter extends BaseAdapter {

    Context context;
    List<SongModel> songModels;
    Utility utility;
    PlayInterface playInterface;
    DB db;
    Offline offline;
    SubscriptionBox subscriptionBox;

    public OfflineAdapter(Context context, List<SongModel> songModels, PlayInterface playInterface, Offline offline){
        this.context = context;
        this.songModels = songModels;
        //this.songModels.add(new SongModel());
        this.utility = new Utility(this.context);
        subscriptionBox = new SubscriptionBox(this.context);
        this.playInterface = playInterface;
        this.db = new DB(this.context);
        this.offline = offline;
    }

    @Override
    public int getCount() {
        return songModels.size();
    }

    @Override
    public Object getItem(int position) {
        return songModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.offline_child_layout,null);
            }
            RelativeLayout songLayout = (RelativeLayout) convertView.findViewById(R.id.songLayout);
            TextView songNumber = (TextView) convertView.findViewById(R.id.songNumber);
            TextView songTitle = (TextView) convertView.findViewById(R.id.songTitle);
            TextView songArtist = (TextView) convertView.findViewById(R.id.songArtist);
            ImageView deleteSong = (ImageView) convertView.findViewById(R.id.deleteSong);
            ImageView playSong = (ImageView) convertView.findViewById(R.id.playSong);
            ImageView premium = (ImageView) convertView.findViewById(R.id.premium);
            utility.setFont(songNumber);
            songNumber.setTextSize(12);
            utility.setFont(songTitle);
            songTitle.setTextSize(16);
            utility.setFont(songArtist);
            songArtist.setTextSize(12);
            if(songModels.get(position).getPremium().equals("Yes")){
                premium.setVisibility(View.VISIBLE);
            }
            else{
                premium.setVisibility(View.GONE);
            }
            songNumber.setText(utility.getLangauge().equals("bn")
                    ? utility.convertToBangle(String.valueOf(position + 1))
                    : String.valueOf(position + 1));
            songTitle.setText(
                    utility.getLangauge().equals("bn")
                            ? Html.fromHtml(songModels.get(position).getTitle_bn())
                            : songModels.get(position).getTitle()
            );
            songArtist.setText(
                    utility.getLangauge().equals("bn")
                            ? Html.fromHtml(songModels.get(position).getArtist_bn())
                            : songModels.get(position).getArtist()
            );
            playSong.setOnClickListener(v -> {
                if(songModels.get(position).getPremium().equals("Yes")){
                    if(subscriptionBox.isSubscribed(String.valueOf(songModels.get(position).getId()))){
                        playInterface.listPlay(songModels, position, true);
                    }
                    else{
                        utility.showToast("Your subscription expired. Please check your status from subscription menu.");
                    }
                }
                else {
                    playInterface.listPlay(songModels, position, true);
                }
            });
            songLayout.setOnClickListener(v -> {
                if(songModels.get(position).getPremium().equals("Yes")){
                    if(subscriptionBox.isSubscribed(String.valueOf(songModels.get(position).getId()))){
                        playInterface.listPlay(songModels, position, true);
                    }
                    else{
                        utility.showToast("Your subscription expired. Please check your status from subscription menu.");
                    }
                }
                else {
                    playInterface.listPlay(songModels, position, true);
                }
            });
            deleteSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(position);
                }
            });
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return convertView;
    }

    private void deleteFile(int position){
        File directory = new File(context.getFilesDir(), "RadioG");
        String filePath = directory.getAbsolutePath() + "/" + songModels.get(position).getId() + ".mp3";
        File file = new File(filePath);
        if(file.exists()){
            if(file.delete()){
                utility.logger("File Deleted");
                dbRecordDelete(position);
            }
            else{
                utility.logger("File Not Deleted");
            }
        }
        else{
            utility.logger("File not exists");
            dbRecordDelete(position);
        }
    }

    private void dbRecordDelete(int position) {
        db.open();
        boolean isDeletedFromDB = db.deleteSong(songModels.get(position).getId());
        db.close();
        if(isDeletedFromDB){
            utility.logger("File Deleted from DB");
        }
        else{
            utility.logger("File Not Deleted from DB");
        }
        db.open();
        songModels = db.getAllSong();
        //songModels.add(new SongModel());
        db.close();
        notifyDataSetChanged();
        if(songModels.size()==0){
            playInterface.lastSongDelete();
            offline.lastFileDeleted();
        }
    }
}
