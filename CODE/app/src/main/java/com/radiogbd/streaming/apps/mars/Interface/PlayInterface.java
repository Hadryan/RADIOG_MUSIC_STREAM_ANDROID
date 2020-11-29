package com.radiogbd.streaming.apps.mars.Interface;

import com.radiogbd.streaming.apps.mars.Model.SongModel;

import java.util.List;

/**
 * Created by Hp on 12/19/2017.
 */

public interface PlayInterface {

    void songPlaye(int position);

    void listPlay(List<SongModel> songModels, int position, boolean isOffline);

    void downloadSong(SongModel songModel);

    void lastSongDelete();

}
