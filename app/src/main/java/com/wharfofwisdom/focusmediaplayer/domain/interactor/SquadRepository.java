package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Message;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface SquadRepository {

    Flowable<Message> waitCommand();

    Single<Squad> announceSquad(Squad squadName);

    Single<Squad> searchSquad(String squadName);

    Single<Squad> createSquad(Kiosk soldier);

    Single<Squad> searchSquad();

    Single<Squad.POSITION> joinSquad(Squad squad);
}
