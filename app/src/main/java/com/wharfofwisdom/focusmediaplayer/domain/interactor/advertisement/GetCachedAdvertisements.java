package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GetCachedAdvertisements {
    private final CacheRepository advertisementRepository;

    public GetCachedAdvertisements(CacheRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public Flowable<List<Advertisement>> execute() {
        return advertisementRepository.getAdvertisements().subscribeOn(Schedulers.io());
    }
}

