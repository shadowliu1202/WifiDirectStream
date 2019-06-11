package com.wharfofwisdom.focusmediaplayer.domain.repository.db.mapper;

import android.net.Uri;
import android.text.TextUtils;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdWithVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvertisementMapper {
    public static List<Advertisement> convert(List<AdWithVideo> adEntities) {
        List<Advertisement> list = new ArrayList<>();
        int index = 0;
        for (AdWithVideo adEntity : adEntities) {
            if (adEntity.videos.size() > 0) {
                Video video = Video.builder()
                        .id(adEntity.videos.get(0).id).index(adEntity.order).name("video")
                        .url(Uri.fromFile(new File(adEntity.videos.get(0).filePath)).getPath())
                        .build();
                list.add(Advertisement.builder()
                        .index(++index)
                        .id(String.valueOf(index))
                        .video(video).build());
            }
        }
        return list;
    }

    public static List<AdEntity> convertToEntity(List<Advertisement> advertisements) {
        List<AdEntity> adEntities = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            adEntities.add(AdEntity.AdEntityBuilder.anAdEntity()
                    .withId(advertisement.id()).withOrder(advertisement.index())
                    .withVideoId(advertisement.video().id()).withVideoUrl(advertisement.video().url().toString()).build());
        }
        return adEntities;
    }
}
