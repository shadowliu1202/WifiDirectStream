package com.wharfofwisdom.focusmediaplayer.domain.repository.p2p;

import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

public class P2PRepository implements SquadRepository {
    private final WifiP2pManager mManager;
    private final WifiP2pManager.Channel mChannel;
    private final WifiP2PReceiver receiver;
    private PublishSubject<WifiP2pInfo> p2pInfoPublishSubject = PublishSubject.create();

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
                p2pInfoPublishSubject.onNext(p2pInfo);
                p2pInfoPublishSubject.onComplete();
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
    public Single<Squad> announceSquad(Squad squad) {
        return addLocalService(squad);
    }


    private Single<Squad> addLocalService(Squad squad) {
        return Single.create(emitter -> {
            Map<String, String> record = new HashMap<>();
            record.put("GroupName", squad.name());
            record.put("buddyname", "這是我的驗證密碼" + (int) (Math.random() * 1000));
            record.put("available", "visible");
            WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("聯網機群組", "_presence._tcp", record);
            mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    emitter.onSuccess(squad);
                }

                @Override
                public void onFailure(int arg0) {
                    // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    emitter.onError(new Exception(String.valueOf(arg0)));
                }
            });
        });
    }

    @Override
    public Single<Squad> searchSquad(String squadName) {
        return Single.never();
    }

    @Override
    public Single<Squad> createSquad(Soldier soldier) {
        return createGroup().andThen(Single.create(emitter -> emitter.onSuccess(Squad.builder().name(soldier.getSquadName()).build())));
    }

    private Completable createGroup() {
        return Completable.fromAction(() -> mManager.createGroup(mChannel, null))
                .andThen(p2pInfoPublishSubject).flatMapCompletable(p2pInfo -> {
                    if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
                        return Completable.complete();
                    }
                    return Completable.error(new Exception("UnExpected Error"));
                });
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
