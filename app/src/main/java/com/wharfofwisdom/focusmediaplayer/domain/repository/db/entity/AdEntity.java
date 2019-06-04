package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AdEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    public int order;
    public String videoId;
    public String videoUrl;
}
