package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;


import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GetLackedVideos {
    private final CacheRepository cacheRepository;
    private final List<Advertisement> advertisements;

    public GetLackedVideos(CacheRepository cacheRepository, List<Advertisement> advertisements) {
        this.cacheRepository = cacheRepository;
        this.advertisements = advertisements;
    }

    public Flowable<List<Advertisement>> execute() {
        return cacheRepository.getNotDownloadAdvertisement(advertisements).subscribeOn(Schedulers.io());
    }
}
