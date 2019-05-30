package com.wharfofwisdom.focusmediaplayer.domain.interactor.command;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


public class JoinSquad {

    private final Soldier soldier;
    private final SquadRepository repository;

    public JoinSquad(Squad squad, Soldier soldier, SquadRepository repository) {
        this.soldier = soldier;
        this.repository = repository;
    }

    public Single<Squad> execute() {
        if (soldier.belongToSquad()) {
            return repository.searchSquad(soldier.getSquadName())
                    .flatMap(repository::joinSquad)
                    .subscribeOn(Schedulers.io());
        }
        if (soldier.isLeader()) {
            return repository.createSquad(soldier)
                    .subscribeOn(Schedulers.io());
        }
        return repository.searchSquad()
                .flatMap(repository::joinSquad)
                .subscribeOn(Schedulers.io());
    }
}
