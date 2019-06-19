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

    @Transaction
    @Query("SELECT * FROM AdEntity WHERE id IN(:adIds)")
    Flowable<List<AdWithVideo>> getAdvertisements(String[] adIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAdvertisements(List<AdEntity> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveVideo(VideoEntity videoEntity);
}
