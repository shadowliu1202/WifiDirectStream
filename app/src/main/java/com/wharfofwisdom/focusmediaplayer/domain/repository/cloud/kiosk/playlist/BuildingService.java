package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist;

import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist.bean.PlayListResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BuildingService {

    @GET("/api/Pois/{id}/playlists")
    Single<List<PlayListResponse>> getPlayList(@Path("id") String buildingId, @Query("filter") String filter);
}
