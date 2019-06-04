package com.wharfofwisdom.focusmediaplayer.domain.repository.db;

import android.content.Context;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao.AdvertisementDao;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.mapper.AdvertisementMapper;

import java.util.List;

import io.reactivex.Flowable;

public class RoomRepository implements AdvertisementRepository {
    private final AdvertisementDao advertisementDao;

    public RoomRepository(Context context) {
        advertisementDao = FocusMediaDatabase.getDatabase(context).advertisementDao();
    }

    @Override
    public Flowable<List<Advertisement>> getAdvertisements() {
        return advertisementDao.getAdvertisements().map(AdvertisementMapper::convert);
    }
}
