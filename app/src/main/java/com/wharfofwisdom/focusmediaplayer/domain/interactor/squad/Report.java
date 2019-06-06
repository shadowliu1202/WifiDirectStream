package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class Report {
    private final SquadRepository squadRepository;
    private final Mission mission;

    public Report(SquadRepository squadRepository, Mission mission) {
        this.squadRepository = squadRepository;
        this.mission = mission;
    }

    public Completable execute() {
        return squadRepository.request(mission).subscribeOn(Schedulers.io());
    }
}
