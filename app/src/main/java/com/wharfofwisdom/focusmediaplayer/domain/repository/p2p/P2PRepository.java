package com.wharfofwisdom.focusmediaplayer.domain.repository.p2p;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import java.util.List;

import io.reactivex.Single;

public class P2PRepository implements SquadRepository {
    private final WifiP2pManager mManager;
    private final WifiP2pManager.Channel mChannel;
    private final WifiP2PReceiver receiver;

    public P2PRepository(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.receiver = new WifiP2PReceiver(mChannel, mManager, new WifiP2PReceiver.EventListener() {
            @Override
            public void onErrors(Throwable throwable) {

            }

            @Override
            public void onPeersChanged(List<WifiP2pDevice> peers) {

            }

            @Override
            public void onConnected(WifiP2pInfo p2pInfo, NetworkInfo networkInfo) {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onInformation(String string) {

            }
        });
    }

    @Override
    public Single<Squad> searchSquad(String squadName) {
        return Single.never();
    }

    @Override
    public Single<Squad> createSquad(Soldier soldier) {
        return Single.never();
    }

    @Override
    public Single<Squad> searchSquad() {
        return Single.never();
    }

    @Override
    public Single<Squad> joinSquad(Squad squad) {
        return Single.never();
    }

    public WifiP2PReceiver getReceiver() {
        return receiver;
    }
}
