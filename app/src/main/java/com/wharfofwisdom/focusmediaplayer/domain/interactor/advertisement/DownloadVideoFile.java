package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import android.net.Uri;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DownloadVideoFile {
    private final VideoRepository repository;
    private final Uri uri;

    public DownloadVideoFile(VideoRepository repository, Uri uri) {
        this.repository = repository;
        this.uri = uri;
    }

    public Single<File> execute() {
        return repository.getFile(uri).subscribeOn(Schedulers.io());
    }
}
