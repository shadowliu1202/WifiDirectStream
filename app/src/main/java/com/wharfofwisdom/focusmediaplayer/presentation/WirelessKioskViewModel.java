package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.Report;

import io.reactivex.Completable;

public class WirelessKioskViewModel extends ViewModel {
    private final SquadRepository squadRepository;
    private final CacheRepository cacheRepository;

    WirelessKioskViewModel(SquadRepository squadRepository, CacheRepository cacheRepository) {
        this.squadRepository = squadRepository;
        this.cacheRepository = cacheRepository;
    }

    //附屬連網機-啟動順序
    public Completable start() {
        //要求這禮拜的播放清單
        return new Report(squadRepository, new RequestPlayList()).execute();
    }
}
