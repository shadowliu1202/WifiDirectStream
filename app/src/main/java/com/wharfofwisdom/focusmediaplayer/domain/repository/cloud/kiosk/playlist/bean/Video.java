package com.wharfofwisdom.focumediaplayer.domain.repository.cloud.kiosk.playlist.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("md5")
    @Expose
    public String md5;
    @SerializedName("isArchived")
    @Expose
    public Boolean isArchived;
    @SerializedName("youtubeId")
    @Expose
    public String youtubeId;
    @SerializedName("width")
    @Expose
    public Integer width;
    @SerializedName("height")
    @Expose
    public Integer height;
    @SerializedName("duration")
    @Expose
    public Integer duration;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;

}
