package com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface AdvertisementDao {
    @Query("SELECT * from AdEntity where filePath IsNull ORDER BY `order` DESC")
    Flowable<List<AdEntity>> getAdvertisements();
}
