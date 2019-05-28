package com.wharfofwisdom.focusmediaplayer.presentation;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvertisementViewModel extends ViewModel {
    private final MutableLiveData<List<Advertisement>> advertisements = new MutableLiveData<>();
    private final MutableLiveData<List<Video>> playlist = new MutableLiveData<>();
    private final List<Video> videos = new ArrayList<>();

    public AdvertisementViewModel() {
        advertisements.postValue(getAdvertisementList());
    }

    private List<Advertisement> getAdvertisementList() {
        List<Advertisement> advertisements = new ArrayList<>();
        advertisements.add(Advertisement.builder()
                .id("0")
                .index(0)
                .video(Video.builder()
                        .index(0)
                        .id("5915bd627ce91c3851f43c5e")
                        .name("人生走馬燈篇")
                        .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596928064-人生走馬燈篇.mp4"))
                        .build())
                .build());
        advertisements.add(Advertisement.builder()
                .id("1")
                .index(1)
                .video(Video.builder()
                        .index(1)
                        .id("5915bd7d7ce91c3851f43c5f")
                        .name("健檢篇")
                        .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596984200-健檢篇.mp4"))
                        .build())
                .build());
        advertisements.add(Advertisement.builder()
                .index(2)
                .id("2")
                .video(Video.builder().id("df1c790ae436eb1ff374103e5d8bbf44")
                        .index(2)
                        .name("財政部國稅局")
                        .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494597036850-憑證報稅台.mp4"))
                        .build())
                .build());
        return advertisements;
    }

    LiveData<List<Advertisement>> getAdvertisements() {
        return advertisements;
    }

    LiveData<List<Video>> getPlayList() {
        return playlist;
    }

    synchronized void addToPlayList(Video video) {
        if (!videos.contains(video)) {
            videos.add(video);
        }
        Collections.sort(videos, (o1, o2) -> o1.index() - o2.index());
        playlist.postValue(videos);
    }
}
