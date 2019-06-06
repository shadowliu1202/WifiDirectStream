package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

public class RequestPlayList extends Mission {
    @Override
    public String message() {
        return getClass().getSimpleName();
    }

    @Override
    public String mission() {
        return null;
    }
}
