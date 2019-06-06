package com.wharfofwisdom.focusmediaplayer.domain.model.hardware;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Kiosk : Consider as a General Kiosk.
 */
public abstract class Kiosk {

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

    public Flowable<Void> start() {
        return null;

    }
}
