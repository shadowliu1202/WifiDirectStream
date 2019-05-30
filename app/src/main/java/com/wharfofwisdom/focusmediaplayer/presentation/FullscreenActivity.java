package com.wharfofwisdom.focusmediaplayer.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
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
import com.wharfofwisdom.focusmediaplayer.DownloadTracker;
import com.wharfofwisdom.focusmediaplayer.R;
import com.wharfofwisdom.focusmediaplayer.demo.ClientInit;
import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.demo.MessageService;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;
import com.wharfofwisdom.focusmediaplayer.presentation.service.DemoDownloadService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FullscreenActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();
    private final IntentFilter intentFilter = new IntentFilter();
    private DownloadTracker downloadTracker;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private WifiP2PReceiver receiver;
    WifiP2pDnsSdServiceRequest serviceRequest;
    public static boolean isMaster = true;

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
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        downloadTracker = ((DemoApplication) getApplication()).getDownloadTracker();
//        JsonObject object = new JsonObject();
//        object.addProperty("limit", 1);
        //new KioskClient(this).getBuildingService().getPlayList("58bcf5d568ba196d0b19ad4e", object.toString()

        AdvertisementViewModel viewModel = ViewModelProviders.of(this).get(AdvertisementViewModel.class);
        viewModel.getPlayList().observe(this, this::playVideoFromCache);
        compositeDisposable.add(Flowable.fromIterable(getAdvertisementList())
                .subscribeOn(Schedulers.io())
                .flatMap(advertisement -> {
                    Log.d("Test", "Get:" + advertisement.video().name());
                    if (downloadTracker.isDownloaded(advertisement.video().url())) {
                        return Flowable.just(advertisement.video());
                    }
                    return download(advertisement).toFlowable();
                }).subscribe(viewModel::addToPlayList, Throwable::printStackTrace));

        try {
            DownloadService.start(this, DemoDownloadService.class);
        } catch (IllegalStateException e) {
            DownloadService.startForeground(this, DemoDownloadService.class);
        }
        //Start p2p Listener
        initWifiP2P();
        //Start the service to receive message
        startService(new Intent(this, MessageService.class));

        if (isMaster) {
            initMasterFocusMediaService(mManager, mChannel);
            discoverPeers(mManager, mChannel);
        } else {
            addClientServiceListner(mManager, mChannel);
            discoverFocusMediaService(mManager, mChannel);
        }
    }

    private void discoverPeers(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Test", "discoverPeers onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("Test", "discoverPeers onFailure");
            }
        });
    }

    private void addClientServiceListner(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        final HashMap<String, String> buddies = new HashMap<>();
        //可获取其他servece广播的信息
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */
        WifiP2pManager.DnsSdTxtRecordListener txtListener = (fullDomain, record, device) -> {
            Log.d("Test", "DnsSdTxtRecord available -" + fullDomain + ":" + record.toString());
            if (Objects.requireNonNull(record.get("host")).contains("小米")) {
                connectService(device);
            }
            buddies.put(device.deviceAddress, record.get("buddyname"));
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = (instanceName, registrationType, resourceType) -> {
            // Update the device name with the human-friendly version from
            // the DnsTxtRecord, assuming one arrived.
            resourceType.deviceName = buddies
                    .containsKey(resourceType.deviceAddress) ? buddies
                    .get(resourceType.deviceAddress) : resourceType.deviceName;

            // Add to the custom adapter defined specifically for showing
            // wifi devices.
            Log.d("Test", "onBonjourServiceAvailable " + instanceName);
        };

        manager.setDnsSdResponseListeners(channel, servListener, txtListener);

    }

    private void connectService(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        if (serviceRequest != null)
            //连接时removeServiceRequest
            mManager.removeServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int arg0) {
                }
            });

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("Test", "Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d("Test", "Failed connecting to service");
            }
        });
    }

    private void discoverFocusMediaService(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        //必须先进行addServiceRequest才能进行查找
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("Test", "Added service discovery request");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d("Test", "Failed adding service discovery request");
            }
        });

        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("Test", "Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d("Test", "Service discovery failed");

            }
        });

    }

    private void initMasterFocusMediaService(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        Map<String, String> record = new HashMap<>();
        record.put("host", "小米 mi");
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d("Test", "addLocalService onSuccess");
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d("Test", "addLocalService onFailure");
            }
        });
    }

    private void initWifiP2P() {
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
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
    protected void onResume() {
        super.onResume();
        receiver = new WifiP2PReceiver(mChannel, mManager);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        player.stop();
    }

    private void playVideoFromCache(List<Video> playListResponses) {
        Log.d("Test", "playVideoFromCache:" + playListResponses.size());
        DataSource.Factory dataSourceFactory = ((DemoApplication) getApplication()).buildDataSourceFactory();
        // This is the MediaSource representing the media to be played.
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
