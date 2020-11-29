package com.radiogbd.streaming.apps.mars.Model;

import java.io.Serializable;

/**
 * Created by Hp on 12/4/2017.
 */

public class SongModel implements Serializable {

    int id;
    String title;
    String title_bn;
    String artist;
    String artist_bn;
    String album;
    String album_bn;
    String lyrics;
    String lyrics_bn;
    String tune;
    String tune_bn;
    String link;
    String tokenFav;
    String release_date;
    String albumImage;
    String localPath;
    String status;
    String premium;

    public SongModel() {
    }

    public SongModel(int id){
        this.id = id;
    }

    public SongModel(int id, String title, String title_bn, String artist, String artist_bn, String album, String album_bn, String lyrics, String lyrics_bn, String tune, String tune_bn, String link, String tokenFav, String release_date, String albumImage) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.artist = artist;
        this.artist_bn = artist_bn;
        this.album = album;
        this.album_bn = album_bn;
        this.lyrics = lyrics;
        this.lyrics_bn = lyrics_bn;
        this.tune = tune;
        this.tune_bn = tune_bn;
        this.link = link;
        this.tokenFav = tokenFav;
        this.release_date = release_date;
        this.albumImage = albumImage;
    }

    public SongModel(int id, String title, String title_bn, String artist, String artist_bn, String album, String album_bn, String lyrics, String lyrics_bn, String tune, String tune_bn, String link, String tokenFav, String release_date, String albumImage, String localPath, String status) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.artist = artist;
        this.artist_bn = artist_bn;
        this.album = album;
        this.album_bn = album_bn;
        this.lyrics = lyrics;
        this.lyrics_bn = lyrics_bn;
        this.tune = tune;
        this.tune_bn = tune_bn;
        this.link = link;
        this.tokenFav = tokenFav;
        this.release_date = release_date;
        this.albumImage = albumImage;
        this.localPath = localPath;
        this.status = status;
    }

    public SongModel(int id, String title, String title_bn, String artist, String artist_bn, String album, String album_bn, String lyrics, String lyrics_bn, String tune, String tune_bn, String link, String tokenFav, String release_date, String albumImage, String localPath, String status, String premium) {
        this.id = id;
        this.title = title;
        this.title_bn = title_bn;
        this.artist = artist;
        this.artist_bn = artist_bn;
        this.album = album;
        this.album_bn = album_bn;
        this.lyrics = lyrics;
        this.lyrics_bn = lyrics_bn;
        this.tune = tune;
        this.tune_bn = tune_bn;
        this.link = link;
        this.tokenFav = tokenFav;
        this.release_date = release_date;
        this.albumImage = albumImage;
        this.localPath = localPath;
        this.status = status;
        this.premium = premium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTokenFav() {
        return tokenFav;
    }

    public void setTokenFav(String tokenFav) {
        this.tokenFav = tokenFav;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public String getTitle_bn() {
        return title_bn;
    }

    public void setTitle_bn(String title_bn) {
        this.title_bn = title_bn;
    }

    public String getArtist_bn() {
        return artist_bn;
    }

    public void setArtist_bn(String artist_bn) {
        this.artist_bn = artist_bn;
    }

    public String getAlbum_bn() {
        return album_bn;
    }

    public void setAlbum_bn(String album_bn) {
        this.album_bn = album_bn;
    }

    public String getLyrics_bn() {
        return lyrics_bn;
    }

    public void setLyrics_bn(String lyrics_bn) {
        this.lyrics_bn = lyrics_bn;
    }

    public String getTune_bn() {
        return tune_bn;
    }

    public void setTune_bn(String tune_bn) {
        this.tune_bn = tune_bn;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
}
