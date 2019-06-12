package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.NullMission;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestLackedVideos;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.RequestPlayList;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission.UpdatePlayList;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

public class MissionFactory {
    public static Mission create(String mission, String message) {
        switch (mission) {
            case "RequestPlayList":
                return new RequestPlayList();
            case "UpdatePlayList":
                return new UpdatePlayList(message);
            case "RequestLackedVideos":
                return new RequestLackedVideos(message);
            default:
                return NullMission.createInstance();
        }
    }
}
