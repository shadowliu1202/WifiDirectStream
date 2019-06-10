package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class Waiting {
    private final SquadRepository squadRepository;

    public Waiting(SquadRepository squadRepository) {
        this.squadRepository = squadRepository;
    }

    public Flowable<Mission> execute() {
        return squadRepository.waitCommand().subscribeOn(Schedulers.io());
    }
}
