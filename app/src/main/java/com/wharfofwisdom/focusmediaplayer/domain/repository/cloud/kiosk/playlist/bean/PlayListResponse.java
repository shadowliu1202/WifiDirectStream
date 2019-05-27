
package com.wharfofwisdom.focumediaplayer.domain.repository.cloud.kiosk.playlist.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlayListResponse {

    @SerializedName("startTime")
    @Expose
    public String startTime;
    @SerializedName("endTime")
    @Expose
    public String endTime;
    @SerializedName("list")
    @Expose
    public java.util.List<PlayInfo> list = new ArrayList<>();
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;
    @SerializedName("poi")
    @Expose
    public Poi poi;

}
