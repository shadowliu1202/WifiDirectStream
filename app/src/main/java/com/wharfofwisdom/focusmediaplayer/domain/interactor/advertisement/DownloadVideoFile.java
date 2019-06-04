package com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement;

import android.net.Uri;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.FileRepository;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class DownloadVideoFile {
    private final FileRepository repository;
    private final Uri uri;

    public DownloadVideoFile(FileRepository repository, Uri uri) {
        this.repository = repository;
        this.uri = uri;
    }

    public Single<File> execute() {
        return repository.getFile(uri).subscribeOn(Schedulers.io());
    }
}
