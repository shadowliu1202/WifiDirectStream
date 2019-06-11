package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement.GetCachedAdvertisements;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement.GetLackedVideos;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestLackedVideos;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdatePlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.Report;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.Waiting;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.subjects.BehaviorSubject;

public class WirelessKioskViewModel extends ViewModel {
    private final SquadRepository squadRepository;
    private final CacheRepository cacheRepository;
    private final AdvertisementRepository advertisementRepository;
    private final BehaviorSubject<Squad> squadBehavior = BehaviorSubject.create();


    WirelessKioskViewModel(SquadRepository squadRepository, CacheRepository cacheRepository, AdvertisementRepository advertisementRepository) {
        this.squadRepository = squadRepository;
        this.cacheRepository = cacheRepository;
        this.advertisementRepository = advertisementRepository;
    }

    public void setSquad(Squad squad) {
        squadBehavior.onNext(squad);
    }

    //附屬連網機-啟動順序
    Completable start() {
        //要求這禮拜的播放清單
        return report(new RequestPlayList());
    }

    Completable waiting() {
        return squadBehavior.toFlowable(BackpressureStrategy.LATEST)
                .concatMapCompletable(squad -> new Waiting(squadRepository).execute().flatMapCompletable(mission -> doMission(squad, mission)))
                .repeat();
    }

    private Completable doMission(Squad squad, Mission mission) {
        if (mission instanceof UpdatePlayList) {
            return ((UpdatePlayList) mission).execute(cacheRepository)
                    .andThen(new GetCachedAdvertisements(advertisementRepository).execute().firstElement())
                    .flatMap(advertisements -> new GetLackedVideos(cacheRepository, advertisements).execute().firstElement())
                    .flatMapCompletable(videos->report(new RequestLackedVideos(videos)));
        }
        return Completable.never();
    }

    private Completable report(Mission mission) {
        return new Report(squadRepository, mission).execute();
    }
}
