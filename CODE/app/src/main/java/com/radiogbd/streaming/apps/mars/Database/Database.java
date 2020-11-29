package com.radiogbd.streaming.apps.mars.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hp on 1/8/2018.
 */

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "radiog";
    public static final int DATABASE_VERSION = 1;

    public static final String SONG_ID = "id";
    public static final String SONG_TITLE = "title";
    public static final String SONG_TITLE_BN = "title_bn";
    public static final String SONG_ARTIST = "artist";
    public static final String SONG_ARTIST_BN = "artist_bn";
    public static final String SONG_ALBUM = "album";
    public static final String SONG_ALBUM_BN = "album_bn";
    public static final String SONG_LYRICS = "lyrics";
    public static final String SONG_LYRICS_BN = "lyrics_bn";
    public static final String SONG_TUNE = "tune";
    public static final String SONG_TUNE_BN = "tune_bn";
    public static final String SONG_LINK = "link";
    public static final String SONG_FAV = "tokenFav";
    public static final String SONG_PREMIUM = "premium_layout";
    public static final String SONG_DATE = "release_date";
    public static final String SONG_IMAGE = "image";
    public static final String SONG_PATH = "path";
    public static final String SONG_STATUS = "status";


    public static final String TABLE_SONG = "tbl_song";

    public static final String CREATE_TABLE_SONG = "CREATE TABLE "+
            TABLE_SONG +" ("+ SONG_ID +" INTEGER,"+
            SONG_TITLE+" TEXT,"+
            SONG_TITLE_BN+" TEXT,"+
            SONG_ARTIST+" TEXT,"+
            SONG_ARTIST_BN+" TEXT,"+
            SONG_ALBUM+" TEXT,"+
            SONG_ALBUM_BN+" TEXT,"+
            SONG_LYRICS+" TEXT,"+
            SONG_LYRICS_BN+" TEXT,"+
            SONG_TUNE+" TEXT,"+
            SONG_TUNE_BN+" TEXT,"+
            SONG_LINK+" TEXT,"+
            SONG_FAV+" TEXT,"+
            SONG_PREMIUM+" TEXT,"+
            SONG_DATE+" TEXT,"+
            SONG_IMAGE+" TEXT,"+
            SONG_PATH+" TEXT,"+
            SONG_STATUS+" TEXT)";

    public static final String DROP_TABLE_SONG = "DROP TABLE IF EXISTS "+TABLE_SONG;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SONG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_SONG);
    }
}
