package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;

@Entity
public class AdEntity {
    public String id;
    public int order;
    public String videoId;
    public String videoUrl;
    @Nullable
    public String filePath;

}
