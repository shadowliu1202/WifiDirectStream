package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface AdvertisementRepository {
    Flowable<List<Advertisement>> getAdvertisements();

    Completable addVideoCache(File file, String advertisementId, String videoId);

    Completable setAdvertisements(List<Advertisement> advertisements);

}
