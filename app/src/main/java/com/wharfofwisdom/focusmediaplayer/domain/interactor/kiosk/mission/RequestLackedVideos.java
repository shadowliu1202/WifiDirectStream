package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.google.gson.Gson;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import java.util.List;

public class RequestLackedVideos extends Mission {

    private final List<Video> videos;

    public RequestLackedVideos(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public String message() {
        return new Gson().toJson(videos);
    }

    @Override
    public String mission() {
        return "RequestVideos";
    }
}
