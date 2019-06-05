package com.wharfofwisdom.focusmediaplayer.domain.interactor.squad;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SearchSquad {
    private final SquadRepository repository;
    private final String name;

    public SearchSquad(Squad squad, SquadRepository repository) {
        this.name = squad.name();
        this.repository = repository;
    }

    public SearchSquad(SquadRepository repository) {
        this.name = "";
        this.repository = repository;
    }

    public Single<Squad> execute() {
        if (name.isEmpty()) {
            return repository.searchSquad().flatMap(repository::joinSquad).subscribeOn(Schedulers.io());
        }
        return repository.searchSquad(name).flatMap(repository::joinSquad).subscribeOn(Schedulers.io());
    }
}
