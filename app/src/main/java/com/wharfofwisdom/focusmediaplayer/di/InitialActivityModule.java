package com.wharfofwisdom.focusmediaplayer.di;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;

import com.wharfofwisdom.focusmediaplayer.InitialActivity;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("unused")
@Module
abstract class InitialActivityModule {

    @Provides
    WifiP2pManager provideWifiP2pManager(InitialActivity activity) {
        return (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
    }
//
//    @Provides
//    WifiP2pManager.Channel provideWifiP2pManagerChannel(WifiP2pManager mManager, Activity activity) {
//        return mManager.initialize(activity, activity.getMainLooper(), null);
//    }
//
//    @Provides
//    P2PRepository provideP2PRepository(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
//        return new P2PRepository(mManager, mChannel);
//    }
//
//    @Provides
//    SquadRepository provideSquadRepository(P2PRepository p2PRepository) {
//        return p2PRepository;
//    }

}
