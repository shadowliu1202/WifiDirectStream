package com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdWithVideo;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.VideoEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface AdvertisementDao {
    @Query("SELECT id, `order` FROM AdEntity ORDER BY `order` DESC")
    Flowable<List<AdWithVideo>> getAdvertisements();


    @Query("SELECT * FROM VideoEntity WHERE adId IN(:adIds)")
    Flowable<List<VideoEntity>> getVideos(String[] adIds);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAdvertisements(List<AdEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAdvertisements(AdEntity entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveVideo(VideoEntity videoEntity);
}
