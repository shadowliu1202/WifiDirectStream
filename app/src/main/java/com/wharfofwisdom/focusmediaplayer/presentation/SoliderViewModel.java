package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;

public class SoliderViewModel extends ViewModel {
    private final Kiosk soldier;

    public SoliderViewModel(Kiosk soldier) {
        this.soldier = soldier;
    }

//    Flowable<String> start(){
//        return soldier.startMission()
//                .andThen();
//    }
}
