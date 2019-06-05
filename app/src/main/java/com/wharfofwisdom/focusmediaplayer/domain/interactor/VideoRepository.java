package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import android.net.Uri;

import java.io.File;

import io.reactivex.Single;

public interface VideoRepository {

    Single<File> getFile(Uri uri);
}
