package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GetLoadedAdvertisements {
    private final AdvertisementRepository advertisementRepository;

    public GetLoadedAdvertisements(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }

    public Flowable<List<Advertisement>> execute() {
        return advertisementRepository.getAdvertisements().subscribeOn(Schedulers.io());
    }
}

