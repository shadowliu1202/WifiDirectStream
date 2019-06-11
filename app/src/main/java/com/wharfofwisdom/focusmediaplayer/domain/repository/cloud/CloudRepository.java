package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.KioskClient;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.file.FileService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

public class CloudRepository implements VideoRepository, AdvertisementRepository {
    private final FileService fileService;

    public CloudRepository(Context context) {
        this.fileService = new KioskClient(context).getFileService();
    }

    @Override
    public Single<File> getFile(Uri uri) {
        return fileService.downloadFile(uri.toString()).flatMap(response -> saveToDisk(response, uri));
    }

    private Single<File> saveToDisk(Response<ResponseBody> response, Uri uri) {
        return Single.create(emitter -> {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsoluteFile(), uri.getLastPathSegment());
                if (file.exists() && file.length() > 0) {
                    emitter.onSuccess(file);
                    return;
                }
                Log.d("Test", "save To :" + file.getAbsolutePath());
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                assert response.body() != null;
                sink.writeAll(response.body().source());
                sink.close();
                emitter.onSuccess(file);
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    @Override
    public Flowable<List<Advertisement>> getAdvertisements() {
        return Flowable.just(fakeAdvertisementList());
    }

    private List<Advertisement> fakeAdvertisementList() {
        List<Advertisement> advertisements = new ArrayList<>();
        advertisements.add(Advertisement.builder()
                .id("1")
                .index(0)
                .video(Video.builder()
                        .index(0)
                        .id("5915bd627ce91c3851f43c5e")
                        .name("人生走馬燈篇")
                        .url("https://focusmedia-kiosk.s3.amazonaws.com/1494596928064-人生走馬燈篇.mp4")
                        .build())
                .build());
        advertisements.add(Advertisement.builder()
                .id("5915bd627ce91c3851f43c5e")
                .index(1)
                .video(Video.builder()
                        .index(1)
                        .id("5915bd7d7ce91c3851f43c5f")
                        .name("健檢篇")
                        .url("https://focusmedia-kiosk.s3.amazonaws.com/1494596984200-健檢篇.mp4")
                        .build())
                .build());
        advertisements.add(Advertisement.builder()
                .index(2)
                .id("2")
                .video(Video.builder().id("df1c790ae436eb1ff374103e5d8bbf44")
                        .index(2)
                        .name("財政部國稅局")
                        .url("https://focusmedia-kiosk.s3.amazonaws.com/1494597036850-憑證報稅台.mp4")
                        .build())
                .build());
        return advertisements;
    }
}
