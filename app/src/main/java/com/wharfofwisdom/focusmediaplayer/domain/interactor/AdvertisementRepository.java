package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.util.List;

import io.reactivex.Flowable;

public interface AdvertisementRepository {
    Flowable<List<Advertisement>> getAdvertisements();
}
