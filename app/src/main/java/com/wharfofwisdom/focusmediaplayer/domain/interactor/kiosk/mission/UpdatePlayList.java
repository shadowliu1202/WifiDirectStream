package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.google.gson.Gson;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import java.util.List;

public class UpdatePlayList extends Mission {
    private final List<Advertisement> advertisements;

    public UpdatePlayList(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    @Override
    public String message() {
        return new Gson().toJson(advertisements);
    }

    @Override
    public String mission() {
        return "UpdatePlayList";
    }
}
