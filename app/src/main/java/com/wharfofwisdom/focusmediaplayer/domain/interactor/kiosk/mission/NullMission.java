package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

public class NullMission extends Mission {
    private final static NullMission instance = new NullMission();

    public static NullMission createInstance() {
        return instance;
    }

    @Override
    public String message() {
        return "";
    }

    @Override
    public String mission() {
        return "";
    }
}
