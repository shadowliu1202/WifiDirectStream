package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;


public class JoinSquad {

    private final Kiosk soldier;
    private final SquadRepository repository;

    public JoinSquad(Squad squad, Kiosk soldier, SquadRepository repository) {
        this.soldier = soldier;
        this.repository = repository;
    }

    public Single<Squad> execute() {
        if (soldier.belongToSquad()) {
            return repository.searchSquad(soldier.name())
                    .flatMap(repository::joinSquad)
                    .subscribeOn(Schedulers.io());
        }
        if (soldier.hasInternet()) {
            return repository.createSquad(soldier)
                    .subscribeOn(Schedulers.io());
        }
        return repository.searchSquad()
                .flatMap(repository::joinSquad)
                .subscribeOn(Schedulers.io());
    }
}
