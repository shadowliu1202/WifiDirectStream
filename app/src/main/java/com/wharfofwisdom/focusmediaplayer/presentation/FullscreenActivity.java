package com.wharfofwisdom.focusmediaplayer.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.wharfofwisdom.focusmediaplayer.DemoApplication;
import com.wharfofwisdom.focusmediaplayer.R;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageClient;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageServer;
import com.wharfofwisdom.focusmediaplayer.demo.ClientInit;
import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.demo.MessageService;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CommandFactory;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.video.DownloadVideoFile;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Signaller;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.CloudRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;
import com.wharfofwisdom.focusmediaplayer.presentation.service.DemoDownloadService;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class FullscreenActivity extends AppCompatActivity {

    public static final String SQUAD_NAME = "SQUAD_NAME";
    private SimpleExoPlayer player;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2PReceiver receiver;
    public static boolean isMaster = true;
    private File file;
    private Squad squad;
    private Soldier soldier;
    private InetAddress ownerAddress;

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
        squad = getIntent().getParcelableExtra(SQUAD_NAME);
        soldier = CommandFactory.createSolider(false, this);
        P2PRepository repository = startP2PConnection(soldier, squad);
        receiver = repository.getReceiver();


//        AdvertisementViewModel viewModel = ViewModelProviders.of(this).get(AdvertisementViewModel.class);
//        viewModel.getPlayList().observe(this, this::playVideoFromCache);
//        compositeDisposable.add(Flowable.fromIterable(getAdvertisementList())
//                .subscribeOn(Schedulers.io())
//                .flatMap(advertisement -> {
//                    Log.d("Test", "Get:" + advertisement.video().name());
//                    if (downloadTracker.isDownloaded(advertisement.video().url())) {
//                        return Flowable.just(advertisement.video());
//                    }
//                    return download(advertisement).toFlowable();
//                }).subscribe(viewModel::addToPlayList, Throwable::printStackTrace));
//
//        try {
//            DownloadService.start(this, DemoDownloadService.class);
//        } catch (IllegalStateException e) {
//            DownloadService.startForeground(this, DemoDownloadService.class);
//        }
        startService(new Intent(this, MessageService.class));

        compositeDisposable.add(new DownloadVideoFile(new CloudRepository(this), Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596984200-健檢篇.mp4"))
                .execute()
                .doOnSuccess(file -> this.file = file)
                .map(this::createVideoListFromLocal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::playVideoFromCache, Throwable::printStackTrace));
        findViewById(R.id.fb_send).setOnClickListener(v -> {
            sendMessage(String.valueOf(Math.random() * 100), soldier.getClass().getName());
        });
    }

    private P2PRepository startP2PConnection(Soldier soldier, Squad squad) {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiP2pManager mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel mChannel = mManager.initialize(this, getMainLooper(), null);
        mManager.requestConnectionInfo(mChannel, info -> {
            isMaster = info.isGroupOwner;
            ownerAddress = info.groupOwnerAddress;
            Log.d("test", "get:" + info.groupOwnerAddress + ":" + info.toString());
//            ServerInit server = new ServerInit();
//            server.start();
            ClientInit client = new ClientInit(info.groupOwnerAddress);
            client.start();
//            if (FullscreenActivity.this.soldier.isLeader()) {
//
//            } else {
//
//            }
        });
        return new P2PRepository(mManager, mChannel);
    }

    public void sendMessage(String message, String identity) {
        if (soldier instanceof Signaller) {
            Message mes = new Message(Message.TEXT_MESSAGE, "Welcome", null, "Owner");
            mes.setUser_record("Owner");
            Log.e("Test", "Message hydrated, start SendMessageServer AsyncTask");
            new SendMessageServer(this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
        } else {
            Message mes = new Message(Message.TEXT_MESSAGE, "Banjo", null, "Client");
            mes.setUser_record("Client");
            Log.d("test", "sendMessage:" + ownerAddress);
            new SendMessageClient(this, ownerAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            Log.e("Test", "Message hydrated, start SendMessageClient AsyncTask");
        }
    }

    private void sendFile(String toEndpointId) {

    }

//    private void discoverPeers(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
//        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                Log.d("Test", "discoverPeers onSuccess");
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Log.d("Test", "discoverPeers onFailure");
//            }
//        });
//    }


    private Single<Video> download(final Advertisement advertisement) {
        DownloadService.sendAddDownload(this, DemoDownloadService.class,
                new DownloadRequest(advertisement.video().id(),
                        DownloadRequest.TYPE_PROGRESSIVE,
                        advertisement.video().url(),
                        /* streamKeys= */ Collections.emptyList(),
                        /* customCacheKey= */ null,
                        Util.getUtf8Bytes(advertisement.video().name())),
                /* foreground= */ false);
        if (advertisement.id().equals("1")) {
            return Single.just(advertisement.video()).delay(5, TimeUnit.SECONDS);
        }
        return Single.just(advertisement.video());
    }

    private List<Video> createVideoListFromLocal(File file) {
        List<Video> videos = new ArrayList<>();
        videos.add(Video.builder()
                .index(0)
                .id("5915bd7d7ce91c3851f43c5f")
                .name("健檢篇")
                //.url(Uri.parse("https://focusmedia-kiosk.s3.amazonaws.com/1494596928064-人生走馬燈篇.mp4"))
                .url(Uri.fromFile(file))
                .build());
        return videos;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        player.stop();
    }

    @MainThread
    private void playVideoFromCache(List<Video> playListResponses) {
        DataSource.Factory dataSourceFactory = ((DemoApplication) getApplication()).buildDataSourceFactory();
        List<MediaSource> mediaSources = new ArrayList<>();
        for (Video playInfo : playListResponses) {
            mediaSources.add(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(playInfo.url()));
        }
        concatenatedSource.clear();
        concatenatedSource.addMediaSources(mediaSources);
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true);
    }

}
