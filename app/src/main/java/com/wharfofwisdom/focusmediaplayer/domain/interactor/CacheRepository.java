package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface CacheRepository {

    Completable addVideoCache(File file, String advertisementId, String videoId);

    Completable setAdvertisements(final List<Advertisement> advertisements);

    Flowable<List<Advertisement>> getNotDownloadAdvertisement(final List<Advertisement> advertisements);

}
