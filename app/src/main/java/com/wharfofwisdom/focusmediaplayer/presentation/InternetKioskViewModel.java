package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement.SyncVideosCache;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.CacheAdvertisements;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.GetAdvertisements;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import io.reactivex.Completable;
import io.reactivex.Flowable;

class InternetKioskViewModel extends ViewModel {
    private final AdvertisementRepository advertisementRepository;
    private final CacheRepository cacheRepository;
    private final VideoRepository videoRepository;

    InternetKioskViewModel(AdvertisementRepository advertisementRepository, CacheRepository cacheRepository, VideoRepository videoRepository) {
        this.advertisementRepository = advertisementRepository;
        this.cacheRepository = cacheRepository;
        this.videoRepository = videoRepository;
    }

    //網路連網機-啟動順序
    Completable start() {
        //讀取這禮拜的播放清單
        //儲存清單至Local端
        //回報播放清單至群組 <---- TODO
        //開始下載跟更新缺少的影片
        //回報影片至群組  <---- TODO
        return new GetAdvertisements(advertisementRepository).execute()
                .flatMap(advertisements -> new CacheAdvertisements(advertisements, cacheRepository).execute())
                .flatMapCompletable(advertisements -> new SyncVideosCache(advertisements, cacheRepository, videoRepository).execute());
    }

    public Flowable<Mission> waiting() {
        //等待
        return Flowable.never();
    }
}
