package com.wharfofwisdom.focusmediaplayer.presentation;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.action.ReportPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdatePlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.Report;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.Waiting;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class WirelessKioskViewModel extends ViewModel {
    private final SquadRepository squadRepository;
    private final CacheRepository cacheRepository;
    private final BehaviorSubject<Squad> squadBehavior = BehaviorSubject.create();


    WirelessKioskViewModel(SquadRepository squadRepository, CacheRepository cacheRepository) {
        this.squadRepository = squadRepository;
        this.cacheRepository = cacheRepository;
    }

    public void setSquad(Squad squad) {
        squadBehavior.onNext(squad);
    }

    //附屬連網機-啟動順序
    Completable start() {
        //要求這禮拜的播放清單
        return new Report(squadRepository, new RequestPlayList()).execute();
    }

    public Flowable<Mission> waiting() {
        return squadBehavior.toFlowable(BackpressureStrategy.LATEST).switchMap(squad -> new Waiting(squadRepository).execute().flatMap(mission -> doMission(squad, mission)));
    }

    private Flowable<Mission> doMission(Squad squad, Mission mission) {
        if (mission instanceof UpdatePlayList) {
            Log.d("test", "Get UpdatePlayList:" + mission.message());
        }
        return Flowable.never();
    }

}
