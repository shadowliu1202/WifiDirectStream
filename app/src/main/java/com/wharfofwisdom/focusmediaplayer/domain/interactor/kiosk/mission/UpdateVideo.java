package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import org.jetbrains.annotations.NotNull;

public class UpdateVideo extends Mission {
    private final Video video;
    @org.jetbrains.annotations.NotNull
    private final Advertisement advertisement;

    public UpdateVideo(Advertisement advertisement) {
        this.video = advertisement.video();
        this.advertisement = advertisement;
    }

    @Override
    public String message() {
        return video.url();
    }

    @Override
    public String mission() {
        return "UpdateVideo";
    }

    public Video getVideo() {
        return video;
    }


    @NotNull
    public Advertisement getAdvertisement() {
        return advertisement;
    }
}
