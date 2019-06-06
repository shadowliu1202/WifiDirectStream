package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface CacheRepository {

    Completable addVideoCache(File file, String advertisementId, String videoId);

    Completable setAdvertisements(List<Advertisement> advertisements);

    Flowable<List<Advertisement>> getNotDownloadAdvertisement(List<Advertisement> advertisements);

}
