
package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Poi {

    @SerializedName("priority")
    @Expose
    public Integer priority;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;

}
