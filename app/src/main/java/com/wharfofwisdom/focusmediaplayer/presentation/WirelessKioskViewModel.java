package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;

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
        return Completable.complete();
    }
}
