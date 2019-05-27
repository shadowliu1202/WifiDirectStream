package com.wharfofwisdom.focusmediaplayer;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.JsonObject;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.KioskClient;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist.bean.PlayInfo;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist.bean.PlayListResponse;

import java.util.ArrayList;
import java.util.List;

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
        compositeDisposable.add(new KioskClient(this).getBuildingService().getPlayList("58bcf5d568ba196d0b19ad4e", object.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::playVideo, Throwable::printStackTrace));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void playVideo(List<PlayListResponse> playListResponses) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "focusMediaPlayer"));
        // This is the MediaSource representing the media to be played.
        List<MediaSource> mediaSources = new ArrayList<>();
        for (PlayInfo playInfo : playListResponses.get(0).list) {
            mediaSources.add(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(playInfo.video.url)));
        }
        MediaSource[] mediaArray = new MediaSource[mediaSources.size()];
        mediaArray = mediaSources.toArray(mediaArray);
        // Prepare the player with the source.
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(mediaArray);
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true);
    }
}
