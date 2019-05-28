package com.wharfofwisdom.focusmediaplayer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonObject;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.presentation.service.DemoDownloadService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FullscreenActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        PlayerView mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        player = ExoPlayerFactory.newSimpleInstance(this);
        mContentView.setPlayer(player);
        JsonObject object = new JsonObject();
        object.addProperty("limit", 1);
        //new KioskClient(this).getBuildingService().getPlayList("58bcf5d568ba196d0b19ad4e", object.toString()
        compositeDisposable.add(getAdvertisementList()
                .subscribeOn(Schedulers.io())
              //  .flatMap(this::download)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::playVideoFromCache, Throwable::printStackTrace));
//        // Start the download service if it should be running but it's not currently.
//        // Starting the service in the foreground causes notification flicker if there is no scheduled
//        // action. Starting it in the background throws an exception if the app is in the background too
//        // (e.g. if device screen is locked).
        try {
            DownloadService.start(this, DemoDownloadService.class);
        } catch (IllegalStateException e) {
            DownloadService.startForeground(this, DemoDownloadService.class);
        }
    }

    private Single<List<Advertisement>> getAdvertisementList() {
        List<Advertisement> advertisements = new ArrayList<>();
        advertisements.add(Advertisement.builder()
                .id("5915bd627ce91c3851f43c5e")
                .name("人生走馬燈篇")
                .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596928064-人生走馬燈篇.mp4"))
                .build());
        advertisements.add(Advertisement.builder()
                .id("5915bd7d7ce91c3851f43c5f")
                .name("健檢篇")
                .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596984200-健檢篇.mp4"))
                .build());
        advertisements.add(Advertisement.builder()
                .id("df1c790ae436eb1ff374103e5d8bbf44")
                .name("財政部國稅局")
                .url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494597036850-憑證報稅台.mp4"))
                .build());
        return Single.just(advertisements);
    }

    private Single<List<Advertisement>> download(final List<Advertisement> playListResponses) {
        return Completable.create(emitter -> {
            for (Advertisement playInfo : playListResponses) {
                DownloadService.sendAddDownload(this,
                        DemoDownloadService.class,
                        new DownloadRequest(
                                playInfo.id(),
                                DownloadRequest.TYPE_PROGRESSIVE,
                                playInfo.url(),
                                /* streamKeys= */ Collections.emptyList(),
                                /* customCacheKey= */ null,
                                Util.getUtf8Bytes(playInfo.name())),
                        /* foreground= */ false);
            }
            DemoApplication application = (DemoApplication) getApplication();
            application.getDownloadManager().addListener(new DownloadManager.Listener() {
                @Override
                public void onDownloadChanged(DownloadManager downloadManager, Download download) {
                    if (download.isTerminalState()) {
                        if (!emitter.isDisposed()) {
                            Log.d("test", "onDownloadChanged");
                            emitter.onComplete();
                        }

                    }
                }
            });
        }).andThen(Single.just(playListResponses));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        player.stop();
    }

    private void playVideoFromCache(List<Advertisement> playListResponses) {
        DataSource.Factory dataSourceFactory = ((DemoApplication) getApplication()).buildDataSourceFactory();
        // This is the MediaSource representing the media to be played.
        List<MediaSource> mediaSources = new ArrayList<>();
        for (Advertisement playInfo : playListResponses) {
            mediaSources.add(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(playInfo.url()));
        }
        MediaSource[] mediaArray = new MediaSource[mediaSources.size()];
        mediaArray = mediaSources.toArray(mediaArray);
        // Prepare the player with the source.
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mediaArray);
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true);
    }
}
