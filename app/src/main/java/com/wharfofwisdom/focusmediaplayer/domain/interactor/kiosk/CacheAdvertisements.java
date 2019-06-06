package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class CacheAdvertisements {
    private final List<Advertisement> advertisements;
    private final CacheRepository cacheRepository;

    public CacheAdvertisements(List<Advertisement> advertisements, CacheRepository cacheRepository) {
        this.advertisements = advertisements;
        this.cacheRepository = cacheRepository;
    }

    public Flowable<List<Advertisement>> execute() {
        return cacheRepository.setAdvertisements(advertisements).andThen(Flowable.just(advertisements)).subscribeOn(Schedulers.io());
    }

}
