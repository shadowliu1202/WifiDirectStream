package com.wharfofwisdom.focumediaplayer.domain.repository.cloud.kiosk.playlist.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayInfo {

    @SerializedName("video")
    @Expose
    public Video video;

}
