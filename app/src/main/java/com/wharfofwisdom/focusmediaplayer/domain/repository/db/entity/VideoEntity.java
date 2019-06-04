package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VideoEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    @NonNull
    public String adId = "";
    public String videoUrl;
    public String filePath;
}
