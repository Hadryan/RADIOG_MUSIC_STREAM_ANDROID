package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.OfflineAdapter;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Database.DB;
import com.radiogbd.streaming.apps.mars.Interface.PlayInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.R;

import java.io.File;
import java.util.List;

/**
 * Created by Hp on 9/11/2017.
 */

public class Offline extends Fragment {

    Context context;
    Utility utility;
    DB db;
    List<SongModel> songModels = null;
    TextView downloadtext;
    ListView downloadList;
    OfflineAdapter offlineAdapter;
    PlayInterface playInterface;

    public Offline(){}

    @SuppressLint("ValidFragment")
    public Offline(Context context, PlayInterface playInterface){
        this.context = context;
        utility = new Utility(this.context);
        db = new DB(this.context);
        this.playInterface = playInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offline_layout,null);
        downloadtext = (TextView)view.findViewById(R.id.downloadText);
        downloadList = (ListView) view.findViewById(R.id.downloadList);
        db.open();
        songModels = db.getAllSong();
        db.close();
        deleteCorruptedFile();
        if(songModels.size()>0){
            downloadtext.setVisibility(View.GONE);
        }
        offlineAdapter = new OfflineAdapter(context, songModels, playInterface, Offline.this);
        downloadList.setAdapter(offlineAdapter);
        return view;
    }

    private void deleteCorruptedFile(){
        for(int i=0; i<songModels.size(); i++){
            SongModel songModel = songModels.get(i);
            if(songModel.getStatus().equals(KeyWord.DOWNLOAD_CORRUPTED)){
                File file = new File(songModel.getLink());
                if(file.exists()){
                    if(file.delete()){
                        utility.logger(songModel.getId()+" - "+songModel.getTitle()+" deleted");
                        dbRecordDelete(songModel);
                    }
                    else{
                        utility.logger(songModel.getId()+" - "+songModel.getTitle()+" not deleted");
                    }
                }
            }
        }
        db.open();
        songModels = db.getAllSongByStatus(KeyWord.DOWNLOAD_FINISHED);
        db.close();
    }

    private void dbRecordDelete(SongModel songModel) {
        db.open();
        boolean isDeletedFromDB = db.deleteSong(songModel.getId());
        db.close();
        if(isDeletedFromDB){
            utility.logger("File Deleted from DB");
        }
        else{
            utility.logger("File Not Deleted from DB");
        }
    }

    public void lastFileDeleted() {
        downloadtext.setVisibility(View.VISIBLE);
        downloadList.setVisibility(View.GONE);
    }
}
