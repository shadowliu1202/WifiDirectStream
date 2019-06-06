package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;

import io.reactivex.Completable;

public class Report {
    private final SquadRepository squadRepository;

    public Report(SquadRepository squadRepository, RequestPlayList requestPlayList) {
        this.squadRepository = squadRepository;
    }

    public Completable execute() {
        return Completable.complete();
    }
}
