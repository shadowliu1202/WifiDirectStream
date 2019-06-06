package com.wharfofwisdom.focusmediaplayer.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import com.wharfofwisdom.focusmediaplayer.DemoApplication;
import com.wharfofwisdom.focusmediaplayer.R;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageClient;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageServer;
import com.wharfofwisdom.focusmediaplayer.demo.ClientInit;
import com.wharfofwisdom.focusmediaplayer.demo.Entities.MediaFile;
import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.demo.MessageService;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;
import com.wharfofwisdom.focusmediaplayer.domain.executor.KioskFactory;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement.GetCachedAdvertisements;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.InternetKiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.CloudRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.RoomRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;
import com.wharfofwisdom.focusmediaplayer.presentation.service.DemoDownloadService;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AdvertisementActivity extends AppCompatActivity {

    public static final String SQUAD = "SQUAD";
    private SimpleExoPlayer player;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2PReceiver receiver;
    public static boolean isMaster = true;
    private Squad.POSITION squadPosition;
    private Kiosk kiosk;
    private InetAddress ownerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        kiosk = KioskFactory.create(this);
        squadPosition = (Squad.POSITION) getIntent().getSerializableExtra(SQUAD);
        if (kiosk instanceof InternetKiosk) {
            final AdvertisementRepository advertisementRepository = new CloudRepository(this);
            final CacheRepository cacheRepository = new RoomRepository(this);
            final VideoRepository videoRepository = new CloudRepository(this);
            InternetKioskViewModel kioskViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                @SuppressWarnings("unchecked")
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new InternetKioskViewModel(advertisementRepository, cacheRepository, videoRepository);
                }
            }).get(InternetKioskViewModel.class);
            compositeDisposable.add(kioskViewModel.start().doOnError(this::onError).subscribe());
        } else {

        }


        //====== Wifi Direct 相關 設定======
        startService(new Intent(this, MessageService.class));
        P2PRepository repository = startP2PConnection();
        receiver = repository.getReceiver();
        //=================================

        //===========廣告播放 設定===========
        initPlayer();
        compositeDisposable.add(new GetCachedAdvertisements(new RoomRepository(this))
                .execute().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::playAdvertisements));
        //=================================

    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        PlayerView mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setPlayer(player);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.fullscreen_content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        registerReceiver(receiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void playAdvertisements(List<Advertisement> advertisements) {
        List<Video> videos = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            videos.add(advertisement.video());
        }
        playVideoFromCache(videos);
    }

    private P2PRepository startP2PConnection() {
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
            if (info.isGroupOwner) {
                ServerInit server = new ServerInit();
                server.start();
            } else {
                ClientInit client = new ClientInit(info.groupOwnerAddress);
                client.start();
            }
        });
        return new P2PRepository(mManager, mChannel);
    }

    public void sendMessage(String message, String identity) {
        if (kiosk instanceof InternetKiosk) {
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

    private void sendFile(File file) {
        Message mes = new Message(Message.FILE_MESSAGE, "test", null, "Owner");
        MediaFile mediaFile = new MediaFile(this, file.getPath(), Message.FILE_MESSAGE);
        mes.setByteArray(mediaFile.fileToByteArray());
        mes.setFileName(mediaFile.getFileName());
        mes.setChatName("5915bd627ce91c3851f43c5e");
        mes.setmText("人生走馬燈篇");
        if (isMaster) {
            Log.e("test", "Message hydrated, start SendMessageServer AsyncTask");
            new SendMessageServer(this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
        }
    }

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
