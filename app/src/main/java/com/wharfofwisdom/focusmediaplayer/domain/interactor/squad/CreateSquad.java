package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class CreateSquad {
    private final Kiosk soldier;
    private final SquadRepository repository;

    public CreateSquad(Kiosk soldier, SquadRepository repository) {
        this.soldier = soldier;
        this.repository = repository;
    }

    public Single<Squad> execute() {
        return repository.createSquad(soldier).flatMap(repository::announceSquad).subscribeOn(Schedulers.io());
    }
}
