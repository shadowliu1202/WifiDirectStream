package com.wharfofwisdom.focusmediaplayer.domain.model.squad;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;

public class Soldier {
    public Single<Squad> findSquad() {
        return Single.never();
    }

    public boolean belongToSquad() {
        return false;
    }

    public String getSquadName() {
        return "";
    }

    public boolean isLeader() {
        return false;
    }
}
