package com.wharfofwisdom.focusmediaplayer.domain.model.squad;

import io.reactivex.Completable;

/**
 * Solider : Consider as Kiosk without Internet connection.
 */
public class Soldier {

    public boolean belongToSquad() {
        return false;
    }

    public String getSquadName() {
        return "";
    }

    public boolean isLeader() {
        return false;
    }

    public Completable startMission() {
        return Completable.complete();
    }
}
