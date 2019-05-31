package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;

public interface SquadRepository {

    Single<Squad> announceSquad(Squad squadName);

    Single<Squad> searchSquad(String squadName);

    Single<Squad> createSquad(Soldier soldier);

    Single<Squad> searchSquad();

    Single<Squad> joinSquad(Squad squad);
}
