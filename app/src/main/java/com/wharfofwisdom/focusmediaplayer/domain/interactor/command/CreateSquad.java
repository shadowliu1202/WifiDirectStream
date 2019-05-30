package com.wharfofwisdom.focusmediaplayer.domain.interactor.command;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class CreateSquad {
    private final Soldier soldier;
    private final SquadRepository repository;

    public CreateSquad(Soldier soldier, SquadRepository repository) {
        this.soldier = soldier;
        this.repository = repository;
    }

    public Single<Squad> execute() {
        return repository.createSquad(soldier).subscribeOn(Schedulers.io());
    }
}
