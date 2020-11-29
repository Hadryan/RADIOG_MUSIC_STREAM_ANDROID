package com.radiogbd.streaming.apps.mars.Http;


import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.ArtistModel;
import com.radiogbd.streaming.apps.mars.Model.Banner;
import com.radiogbd.streaming.apps.mars.Model.Bkash;
import com.radiogbd.streaming.apps.mars.Model.Config;
import com.radiogbd.streaming.apps.mars.Model.ExclusiveModel;
import com.radiogbd.streaming.apps.mars.Model.GenreModel;
import com.radiogbd.streaming.apps.mars.Model.HitsModel;
import com.radiogbd.streaming.apps.mars.Model.LikeModel;
import com.radiogbd.streaming.apps.mars.Model.Log;
import com.radiogbd.streaming.apps.mars.Model.Master;
import com.radiogbd.streaming.apps.mars.Model.Msisdn;
import com.radiogbd.streaming.apps.mars.Model.MyListModel;
import com.radiogbd.streaming.apps.mars.Model.NewAlbumModel;
import com.radiogbd.streaming.apps.mars.Model.NoticeModel;
import com.radiogbd.streaming.apps.mars.Model.Plantext;
import com.radiogbd.streaming.apps.mars.Model.SearchAlbumModel;
import com.radiogbd.streaming.apps.mars.Model.SearchArtistModel;
import com.radiogbd.streaming.apps.mars.Model.SelectedModel;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Model.SuggestionModel;
import com.radiogbd.streaming.apps.mars.Model.Version;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Developed by Fojle Rabbi Saikat on 1/9/2017.
 * Owned by Bitmakers Ltd.
 * Contact fojle.rabbi@bitmakers-bd.com
 */
public interface ContentApiInterface {

    //@FormUrlEncoded
    @POST("banner")
    Call<List<Banner>> getBanner(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("premium-{page}")
    Call<List<AlbumModel>> getPremiumList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("page") String page);

    @POST("free-{page}")
    Call<List<AlbumModel>> getFreeList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("page") String page);

    @POST("hits")
    Call<List<HitsModel>> getHitsList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("selected")
    Call<List<SelectedModel>> getSelectedList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("mylist")
    Call<List<MyListModel>> getMyList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("highlight-{page}")
    Call<List<ExclusiveModel>> getHighlightList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("page") String page);

    @POST("recent-{page}")
    Call<List<NewAlbumModel>> getRecentList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("page") String page);

    @POST("list_genre")
    Call<List<GenreModel>> getAllCategory(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("{like}")
    Call<LikeModel> getLikeStatus(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("like") String like);

    @POST("{album}")
    Call<List<AlbumModel>> getAlbums(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("album") String album);

    @POST("{artist}")
    Call<List<ArtistModel>> getArtists(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("artist") String album);

    @POST("{album}")
    Call<List<AlbumModel>> getAlbumByCategory(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("album") String album);

    @POST("{album}")
    Call<List<AlbumModel>> getAlbumByArtist(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("album") String album);

    @POST("get_album-{albumId}")
    Call<List<SongModel>> getSongListByAlbum(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("albumId") String albumId);

    @POST("{album}")
    Call<List<AlbumModel>> getSingleAlbum(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("album") String album);

    @POST("{search}")
    Call<List<SongModel>> getTrackListBySearch(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("search") String album);

    @POST("{search}")
    Call<List<SearchAlbumModel>> getAlbumListBySearch(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("search") String album);

    @POST("{search}")
    Call<List<SearchArtistModel>> getArtistListBySearch(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("search") String album);

    @POST("{suggestion}")
    Call<List<SuggestionModel>> getSuggestionList(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("suggestion") String suggestion);

    @POST("{log}")
    Call<Log> logPlayer(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("log") String suggestion);

    @POST("new")
    Call<List<AlbumModel>> newAlbum(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("{album}")
    Call<List<SongModel>> getAlbum(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("album") String albumName);

    @POST("get_album_using_track-{trackId}")
    Call<List<SongModel>> getAlbumByTrack(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("trackId") String trackId);

    @POST("my_msisdn")
    Call<Msisdn> getMdn(@Header("Authorization") String key, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("viewstatus-{trackId}")
    Call<ResponseBody> viewstatus(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("trackId") String trackId);

    @POST("deactivation")
    Call<ResponseBody> deactivation(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("charge-{trackId}-{pin}")
    Call<ResponseBody> activation(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token, @Path("trackId") String trackId, @Path("pin") String pin);

    @POST("version")
    Call<List<Version>> version(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("config")
    Call<List<Config>> getConfig(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("notice")
    Call<List<NoticeModel>> getNotice(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("bkashviewstatus")
    Call<List<Bkash>> bkashViewStatus(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("bkashdeactivation")
    Call<List<Bkash>> bkashDeactivation(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("bkashactivation")
    Call<List<Bkash>> bkashActivation(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);

    @POST("substext")
    Call<List<Plantext>> getplantext(@Header("Authorization") String authorization, @Header("CARRIER") String carrier, @Header("MOBILE") String mobile, @Header("RGTOKEN") String token);
}
