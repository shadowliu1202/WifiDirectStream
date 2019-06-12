package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.action;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdateVideo;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ReportVideos {
    private final SquadRepository squadRepository;
    private final CacheRepository cacheRepository;
    private final String[] ads;

    public ReportVideos(SquadRepository squadRepository, CacheRepository cacheRepository, List<Advertisement> advertisements) {
        this.squadRepository = squadRepository;
        this.cacheRepository = cacheRepository;
        ads = new String[advertisements.size()];
        for (int i = 0; i < advertisements.size(); i++) {
            ads[i] = advertisements.get(i).id();
        }
    }

    public Completable execute() {
        return cacheRepository.getDownloadedAdvertisementAndVideo(ads)
                .firstOrError()
                .flatMapObservable(Observable::fromIterable)
                .concatMapCompletable(advertisement -> squadRepository.announce(new UpdateVideo(advertisement)))
                .subscribeOn(Schedulers.io());
    }
}
