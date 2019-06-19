package com.wharfofwisdom.focusmediaplayer.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.wharfofwisdom.focusmediaplayer.FocusMediaApplication;
import com.wharfofwisdom.focusmediaplayer.R;
import com.wharfofwisdom.focusmediaplayer.demo.ClientInit;
import com.wharfofwisdom.focusmediaplayer.demo.MessageService;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;
import com.wharfofwisdom.focusmediaplayer.domain.executor.KioskFactory;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.VideoRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.advertisement.GetCachedAdvertisements;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.InternetKiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Follower;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Leader;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.CloudRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.RoomRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
    private BroadcastReceiver messageReceiver;
    private Squad.POSITION squadPosition;
    private Kiosk kiosk;
    private InetAddress ownerAddress;
    private P2PRepository p2pRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        kiosk = KioskFactory.create(this);
        squadPosition = (Squad.POSITION) getIntent().getSerializableExtra(SQUAD);
        //====== Wifi Direct 相關 設定======
        p2pRepository = startP2PConnection();
        receiver = p2pRepository.getReceiver();
        messageReceiver = p2pRepository.getBroadcastReceiver();
        //=================================
        if (kiosk instanceof InternetKiosk) {
            final AdvertisementRepository advertisementRepository = new CloudRepository(this);
            final CacheRepository cacheRepository = new RoomRepository(this);
            final VideoRepository videoRepository = new CloudRepository(this);
            InternetKioskViewModel kioskViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                @SuppressWarnings("unchecked")
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new InternetKioskViewModel(advertisementRepository, cacheRepository, videoRepository, p2pRepository);
                }
            }).get(InternetKioskViewModel.class);
            kioskViewModel.setSquad(Leader.builder().name("leader").address("test").build());
            compositeDisposable.add(kioskViewModel.start().doOnError(this::onError).subscribe());
            compositeDisposable.add(kioskViewModel.waiting().doOnError(this::onError).subscribe());
        } else {
            final SquadRepository squadRepository = p2pRepository;
            RoomRepository roomRepository = new RoomRepository(this);
            WirelessKioskViewModel kioskViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
                @NonNull
                @Override
                @SuppressWarnings("unchecked")
                public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                    return (T) new WirelessKioskViewModel(squadRepository, roomRepository, roomRepository);
                }
            }).get(WirelessKioskViewModel.class);
            kioskViewModel.setSquad(Follower.builder().name("follower").address("test").build());
            compositeDisposable.add(kioskViewModel.start().doOnError(this::onError).subscribe());
            compositeDisposable.add(kioskViewModel.waiting().doOnError(this::onError).subscribe());
        }

        //===========廣告播放 設定===========
        initPlayer();
        compositeDisposable.add(new GetCachedAdvertisements(new RoomRepository(this))
                .execute().subscribeOn(Schedulers.io())
                .map(this::filterNoVideoAdvertisements)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::playAdvertisements));
        //=================================

        //=============P2p=================
        Intent intent = new Intent(this, MessageService.class);
        intent.putExtra("isOwner", kiosk instanceof InternetKiosk);
        startService(intent);
        //=================================

    }

    private List<Advertisement> filterNoVideoAdvertisements(List<Advertisement> advertisements) {
        List<Advertisement> results = new ArrayList<>();
        for (Advertisement advertisement : advertisements) {
            if (!advertisement.video().equals(Video.EMPTY)) {
                results.add(advertisement);
            }
        }
        return results;
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
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("NOW"));
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        player.stop();
    }

    @MainThread
    private void playVideoFromCache(List<Video> playListResponses) {
        DataSource.Factory dataSourceFactory = ((FocusMediaApplication) getApplication()).buildDataSourceFactory();
        List<MediaSource> mediaSources = new ArrayList<>();
        for (Video playInfo : playListResponses) {
            mediaSources.add(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(playInfo.url())));
        }
        concatenatedSource.clear();
        concatenatedSource.addMediaSources(mediaSources);
        player.prepare(concatenatedSource);
        player.setPlayWhenReady(true);
    }
}
