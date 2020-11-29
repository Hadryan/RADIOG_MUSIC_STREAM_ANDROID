package com.radiogbd.streaming.apps.mars.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.SongModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 1/8/2018.
 */

public class DB {

    private SQLiteDatabase database;
    private Database helper;
    Context context;
    Utility utility;

    /* ===== DB Constructor ===== */
    public DB(Context ctx){
        context = ctx;
        helper = new Database(ctx);
        utility = new Utility(context);
    }

    /* ===== Database Connection Open ===== */
    public  void open(){
        try {
            database = helper.getWritableDatabase();
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
    }

    /* ===== Database Connection Close ===== */
    public void close(){
        try {
            helper.close();
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
    }

    /* ===== Insert Data into Category Table ===== */
    public boolean insertSong(SongModel songModel) throws SQLException, JSONException {
        try {
            String sql = "INSERT INTO " + Database.TABLE_SONG + " ("
                    + Database.SONG_ID + ","
                    + Database.SONG_TITLE + ","
                    + Database.SONG_TITLE_BN + ","
                    + Database.SONG_ARTIST + ","
                    + Database.SONG_ARTIST_BN + ","
                    + Database.SONG_ALBUM + ","
                    + Database.SONG_ALBUM_BN + ","
                    + Database.SONG_LYRICS + ","
                    + Database.SONG_LYRICS_BN + ","
                    + Database.SONG_TUNE + ","
                    + Database.SONG_TUNE_BN + ","
                    + Database.SONG_LINK + ","
                    + Database.SONG_FAV + ","
                    + Database.SONG_PREMIUM + ","
                    + Database.SONG_DATE + ","
                    + Database.SONG_IMAGE + ","
                    + Database.SONG_PATH + ","
                    + Database.SONG_STATUS + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            statement.clearBindings();
            statement.bindLong(1, songModel.getId());
            statement.bindString(2, songModel.getTitle());
            statement.bindString(3, songModel.getTitle_bn());
            statement.bindString(4, songModel.getArtist());
            statement.bindString(5, songModel.getArtist_bn());
            statement.bindString(6, songModel.getAlbum());
            statement.bindString(7, songModel.getAlbum_bn());
            statement.bindString(8, songModel.getLyrics());
            statement.bindString(9, songModel.getLyrics_bn());
            statement.bindString(10, songModel.getTune());
            statement.bindString(11, songModel.getTune_bn());
            statement.bindString(12, songModel.getLink());
            statement.bindString(13, songModel.getTokenFav());
            statement.bindString(14, songModel.getPremium());
            statement.bindString(15, songModel.getRelease_date()!=null?songModel.getRelease_date():"");
            statement.bindString(16, songModel.getAlbumImage());
            statement.bindString(17, "");
            statement.bindString(18, "start");
            statement.execute();
            database.setTransactionSuccessful();
            database.endTransaction();
            return true;
        }
        catch (Exception ex){
            utility.logger(ex.toString());
            return false;
        }
    }


    /* ===== Update Local File Path into Song Table ===== */
    public boolean updateLocalFilePath(int songId, String filePath){
        try{
            ContentValues cv = new ContentValues();
            cv.put(Database.SONG_LINK, filePath);
            int result = database.update(Database.TABLE_SONG,cv, Database.SONG_ID +"="+songId, null);
            return result > 0;
        }
        catch (Exception ex){
            utility.logger(ex.toString());
            return false;
        }
    }

    /* ===== Update Song Status into Song Table ===== */
    public boolean updateSongStatus(int songId, String status){
        try{
            ContentValues cv = new ContentValues();
            cv.put(Database.SONG_STATUS, status);
            int result = database.update(Database.TABLE_SONG,cv, Database.SONG_ID +"="+songId, null);
            return result > 0;
        }
        catch (Exception ex){
            utility.logger(ex.toString());
            return false;
        }
    }

    /* ===== Delete Song from Song Table ===== */
    public boolean deleteSong(int songId){
        int result = database.delete(Database.TABLE_SONG, Database.SONG_ID +"="+songId, null);
        return result > 0;
    }

    /* ===== Check if Song Exists ===== */
    public boolean isSongExists(int songId){
        try {
            String sql = "SELECT " + Database.SONG_ID + " FROM " + Database.TABLE_SONG + " WHERE " + Database.SONG_ID + " = " + songId;
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getCount() > 0;
            } else {
                return false;
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
            return false;
        }
    }

    /* ===== Get All Song ===== */
    public List<SongModel> getAllSong() {
        List<SongModel> songModels = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + Database.TABLE_SONG;
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SongModel songModel = new SongModel();
                    songModel.setId(cursor.getInt(cursor.getColumnIndex(Database.SONG_ID)));
                    songModel.setTitle(cursor.getString(cursor.getColumnIndex(Database.SONG_TITLE)));
                    songModel.setTitle_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_TITLE_BN)));
                    songModel.setArtist(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST)));
                    songModel.setArtist_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST_BN)));
                    songModel.setArtist_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST_BN)));
                    songModel.setAlbum(cursor.getString(cursor.getColumnIndex(Database.SONG_ALBUM)));
                    songModel.setAlbum_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ALBUM_BN)));
                    songModel.setLyrics(cursor.getString(cursor.getColumnIndex(Database.SONG_LYRICS)));
                    songModel.setLyrics_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_LYRICS_BN)));
                    songModel.setTune(cursor.getString(cursor.getColumnIndex(Database.SONG_TUNE)));
                    songModel.setTune_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_TUNE_BN)));
                    songModel.setLink(cursor.getString(cursor.getColumnIndex(Database.SONG_LINK)));
                    songModel.setTokenFav(cursor.getString(cursor.getColumnIndex(Database.SONG_FAV)));
                    songModel.setPremium(cursor.getString(cursor.getColumnIndex(Database.SONG_PREMIUM)));
                    songModel.setRelease_date(cursor.getString(cursor.getColumnIndex(Database.SONG_DATE)));
                    songModel.setAlbumImage(cursor.getString(cursor.getColumnIndex(Database.SONG_IMAGE)));
                    songModel.setLocalPath(cursor.getString(cursor.getColumnIndex(Database.SONG_PATH)));
                    songModel.setStatus(cursor.getString(cursor.getColumnIndex(Database.SONG_STATUS)));
                    songModels.add(songModel);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return songModels;
    }

    /* ===== Get All Song By Status ===== */
    public List<SongModel> getAllSongByStatus(String status) {
        List<SongModel> songModels = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + Database.TABLE_SONG + " WHERE " + Database.SONG_STATUS + " = '" + status + "'";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    SongModel songModel = new SongModel();
                    songModel.setId(cursor.getInt(cursor.getColumnIndex(Database.SONG_ID)));
                    songModel.setTitle(cursor.getString(cursor.getColumnIndex(Database.SONG_TITLE)));
                    songModel.setTitle_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_TITLE_BN)));
                    songModel.setArtist(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST)));
                    songModel.setArtist_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST_BN)));
                    songModel.setArtist_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ARTIST_BN)));
                    songModel.setAlbum(cursor.getString(cursor.getColumnIndex(Database.SONG_ALBUM)));
                    songModel.setAlbum_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_ALBUM_BN)));
                    songModel.setLyrics(cursor.getString(cursor.getColumnIndex(Database.SONG_LYRICS)));
                    songModel.setLyrics_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_LYRICS_BN)));
                    songModel.setTune(cursor.getString(cursor.getColumnIndex(Database.SONG_TUNE)));
                    songModel.setTune_bn(cursor.getString(cursor.getColumnIndex(Database.SONG_TUNE_BN)));
                    songModel.setLink(cursor.getString(cursor.getColumnIndex(Database.SONG_LINK)));
                    songModel.setTokenFav(cursor.getString(cursor.getColumnIndex(Database.SONG_FAV)));
                    songModel.setPremium(cursor.getString(cursor.getColumnIndex(Database.SONG_PREMIUM)));
                    songModel.setRelease_date(cursor.getString(cursor.getColumnIndex(Database.SONG_DATE)));
                    songModel.setAlbumImage(cursor.getString(cursor.getColumnIndex(Database.SONG_IMAGE)));
                    songModel.setLocalPath(cursor.getString(cursor.getColumnIndex(Database.SONG_PATH)));
                    songModel.setStatus(cursor.getString(cursor.getColumnIndex(Database.SONG_STATUS)));
                    songModels.add(songModel);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
        return songModels;
    }

}
