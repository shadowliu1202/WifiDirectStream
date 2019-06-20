package com.wharfofwisdom.focusmediaplayer.di;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;

import com.wharfofwisdom.focusmediaplayer.InitialActivity;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;
import dagger.Module;
import dagger.Provides;


@SuppressWarnings("unused")
@Module
class InitialActivityModule {

    @ActivityScope
    @Provides
    WifiP2pManager provideWifiP2pManager(InitialActivity activity) {
        return (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
    }

    @ActivityScope
    @Provides
    WifiP2pManager.Channel provideWifiP2pManagerChannel(WifiP2pManager mManager, InitialActivity activity) {
        return mManager.initialize(activity, activity.getMainLooper(), null);
    }

    @ActivityScope
    @Provides
    P2PRepository provideP2PRepository(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        return new P2PRepository(mManager, mChannel);
    }

    @Provides
    SquadRepository provideSquadRepository(P2PRepository p2PRepository) {
        return p2PRepository;
    }

    @Provides
    WifiP2PReceiver provideWifiP2PReceiver(P2PRepository p2PRepository) {
        return p2PRepository.getReceiver();
    }

    @ActivityScope
    @Provides
    IntentFilter provideWifiIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }


}
