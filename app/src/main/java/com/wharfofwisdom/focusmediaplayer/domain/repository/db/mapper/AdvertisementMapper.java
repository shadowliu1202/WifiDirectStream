package com.wharfofwisdom.focusmediaplayer.domain.repository.db.mapper;

import android.net.Uri;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdWithVideo;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.VideoEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdvertisementMapper {
    public static List<Advertisement> convert(List<AdWithVideo> adEntities) {
        List<Advertisement> list = new ArrayList<>();
        for (AdWithVideo adEntity : adEntities) {
            list.add(Advertisement.builder()
                    .index(adEntity.order)
                    .id(adEntity.id)
                    .video(adEntity.videos.size() > 0 ? convert(adEntity.videos.get(0)) : Video.EMPTY).build());
        }
        return list;
    }

    public static List<AdEntity> convertToEntity(List<Advertisement> advertisements) {
        List<AdEntity> adEntities = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            adEntities.add(AdEntity.AdEntityBuilder.anAdEntity()
                    .withId(advertisement.id()).withOrder(advertisement.index())
                    .withVideoId(advertisement.video().id())
                    .withVideoUrl(advertisement.video().url())
                    .build());
        }
        return adEntities;
    }

    private static Video convert(VideoEntity videoEntity) {
        return Video.builder()
                .id(videoEntity.id).name("video")
                .url(Uri.fromFile(new File(videoEntity.filePath)).getPath())
                .build();
    }

    public static List<Video> convertVideos(List<VideoEntity> videoEntities) {
        List<Video> results = new ArrayList<>();
        for (VideoEntity videoEntity : videoEntities) {
            results.add(convert(videoEntity));
        }
        return results;
    }
}
