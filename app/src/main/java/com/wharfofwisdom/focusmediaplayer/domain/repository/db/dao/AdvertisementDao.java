package com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdWithVideo;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.VideoEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface AdvertisementDao {
    @Transaction
    @Query("SELECT id, `order` FROM AdEntity ORDER BY `order` DESC")
    Flowable<List<AdWithVideo>> getAdvertisements();


    @Query("SELECT * FROM VideoEntity WHERE adId IN(:adIds)")
    Flowable<List<VideoEntity>> getVideos(String[] adIds);

    @Query("SELECT * FROM VideoEntity WHERE adId IN(:adIds) AND filePath NOTNULL")
    Flowable<List<VideoEntity>> getDownloadedVideos(String[] adIds);

    @Query("SELECT * FROM VideoEntity")
    Flowable<List<VideoEntity>> getDownloadedVideos();

    @Query("SELECT * FROM VideoEntity")
    Flowable<List<VideoEntity>> getVideos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAdvertisements(List<AdEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAdvertisements(AdEntity entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveVideo(VideoEntity videoEntity);
}
