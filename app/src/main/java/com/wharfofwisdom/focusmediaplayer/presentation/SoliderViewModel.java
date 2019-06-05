package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;

import io.reactivex.Flowable;

public class SoliderViewModel extends ViewModel {
    private final Soldier soldier;

    public SoliderViewModel(Soldier soldier) {
        this.soldier = soldier;
    }

//    Flowable<String> start(){
//        return soldier.startMission()
//                .andThen();
//    }
}
