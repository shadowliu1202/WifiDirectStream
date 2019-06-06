package com.wharfofwisdom.focusmediaplayer.presentation;

import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.RoomRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

public class VideoViewModel extends ViewModel {
    private final Flowable<List<Video>> playlist;

    public VideoViewModel(RoomRepository roomRepository) {
        playlist = ((AdvertisementRepository) roomRepository).getAdvertisements().map(this::getVideos);
    }

    private List<Video> getVideos(List<Advertisement> advertisements) {
        List<Video> videos = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            videos.add(advertisement.video());
        }
        return videos;
    }

    public Flowable<List<Video>> playablelist() {
        return playlist;
    }
}
