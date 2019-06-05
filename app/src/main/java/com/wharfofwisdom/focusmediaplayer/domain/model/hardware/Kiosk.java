package com.wharfofwisdom.focusmediaplayer.domain.model.hardware;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Completable;

/**
 * Solider : Consider as Kiosk without Internet connection.
 */
public class Kiosk {

    protected Squad squad = Squad.NO_SQUAD;

    public Squad squad() {
        return squad;
    }

    public boolean belongToSquad() {
        return false;
    }

    public String name() {
        return "";
    }

    public boolean hasInternet() {
        return false;
    }

    public Completable startMission() {
        return Completable.complete();
    }
}
