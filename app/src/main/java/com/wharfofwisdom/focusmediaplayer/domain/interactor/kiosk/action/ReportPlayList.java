package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.action;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdatePlayList;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class ReportPlayList {
    private final SquadRepository squadRepository;
    private final AdvertisementRepository advertisementRepository;

    public ReportPlayList(SquadRepository squadRepository, AdvertisementRepository advertisementRepository) {
        this.squadRepository = squadRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public Completable execute() {
        return advertisementRepository.getAdvertisements()
                .firstElement()
                .flatMapCompletable(advertisements -> squadRepository.announce(new UpdatePlayList(advertisements)))
                .subscribeOn(Schedulers.io());
    }
}
