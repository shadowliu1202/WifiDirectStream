package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class AdWithVideo {
    public String id;
    public int order;
    @Relation(parentColumn = "id", entityColumn = "adId")
    public List<VideoEntity> videos = new ArrayList<>();
}
