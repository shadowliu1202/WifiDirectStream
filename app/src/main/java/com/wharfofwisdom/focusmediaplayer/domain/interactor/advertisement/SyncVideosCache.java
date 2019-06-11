package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import android.net.Uri;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.schedulers.Schedulers;

public class SyncVideosCache {
    private final List<Advertisement> newAdvertisements;
    private final CacheRepository cacheRepository;
    private final VideoRepository videoRepository;

    public SyncVideosCache(List<Advertisement> newAdvertisements, CacheRepository cacheRepository, VideoRepository videoRepository) {
        this.newAdvertisements = newAdvertisements;
        this.cacheRepository = cacheRepository;
        this.videoRepository = videoRepository;
    }

    public Completable execute() {
        return cacheRepository.getNotDownloadAdvertisement(newAdvertisements)
                .concatMapIterable(advertisements -> advertisements)
                .flatMapCompletable(this::download).subscribeOn(Schedulers.io());
    }

    private Completable download(final Advertisement advertisements) {
        return new DownloadVideoFile(videoRepository, Uri.parse(advertisements.video().url())).execute()
                .flatMapCompletable(file -> saveToDb(file, advertisements));
    }

    private CompletableSource saveToDb(File file, Advertisement advertisements) {
        return cacheRepository.addVideoCache(file, advertisements.id(), advertisements.video().id());
    }
}
