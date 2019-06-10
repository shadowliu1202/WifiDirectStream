package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.NullMission;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

public class MissionFactory {
    public static Mission create(String mission) {
        switch (mission) {
            case "RequestPlayList":
                return new RequestPlayList();
            default:
                return NullMission.createInstance();
        }
    }
}
