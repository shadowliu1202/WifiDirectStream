package com.wharfofwisdom.focusmediaplayer.domain.repository.p2p;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.message.Message;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.nearby.NearByRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public class P2PRepository implements SquadRepository {
    private static final String IdentityGroup = "focusmedia";
    private final WifiP2pManager mManager;
    private final WifiP2pManager.Channel mChannel;
    private final WifiP2PReceiver receiver;
    private PublishSubject<WifiP2pInfo> p2pInfoPublishSubject = PublishSubject.create();
    private static P2PRepository p2PRepository;

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
    public Flowable<Message> waitCommand() {
        return Flowable.empty();
    }

    @Override
    public Single<Squad> announceSquad(Squad squad) {
        return addLocalService(squad);
    }


    private Single<Squad> addLocalService(Squad squad) {
        return Single.create(emitter -> {
            Map<String, String> record = new HashMap<>();
            record.put("GroupName", squad.name());
            record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
            record.put("available", "visible");
            WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(IdentityGroup, "_presence._tcp", record);
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
        return createGroup(soldier);
    }

    private Single<Squad> createGroup(Soldier soldier) {
        return Completable.fromAction(() -> mManager.createGroup(mChannel, null))
                .andThen(p2pInfoPublishSubject)
                .flatMapSingle(p2pInfo -> {
                    if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
                        return Single.just(Squad.builder()
                                .leaderLocation(p2pInfo.groupOwnerAddress.getHostAddress())
                                .name(soldier.getSquadName()).build());
                    }
                    return Single.error(new Exception("UnExpected Error"));
                }).firstOrError();
    }

    @Override
    public Single<Squad> searchSquad() {
        WifiP2pServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        return clearServiceRequest().andThen(addServiceRequest(serviceRequest))
                .andThen(discoverService())
                .flatMap(v -> removeService(v, serviceRequest));
    }

    private Completable clearServiceRequest() {
        return Completable.create(emitter -> mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                emitter.onComplete();
            }

            @Override
            public void onFailure(int reason) {
                emitter.onError(new Exception(String.valueOf(reason)));
            }
        }));
    }

    private Completable addServiceRequest(WifiP2pServiceRequest serviceRequest) {
        return Completable.create(emitter -> mManager.addServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                emitter.onComplete();
            }

            @Override
            public void onFailure(int arg0) {
                emitter.onError(new Exception(String.valueOf(arg0)));
            }
        }));
    }

    private Single<Squad> discoverService() {
        return Single.create(emitter -> mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                final HashMap<String, String> buddies = new HashMap<>();
                WifiP2pManager.DnsSdTxtRecordListener txtListener = (fullDomain, record, device) -> {
                    if (fullDomain.contains(IdentityGroup)) {
                        emitter.onSuccess(Squad.builder().name(record.get("GroupName")).leaderLocation(device.deviceAddress).build());
                    }
                    buddies.put(device.deviceAddress, record.get("buddyname"));
                };
                WifiP2pManager.DnsSdServiceResponseListener servListener = (instanceName, registrationType, resourceType) ->
                        resourceType.deviceName = buddies.containsKey(resourceType.deviceAddress) ? buddies.get(resourceType.deviceAddress) : resourceType.deviceName;
                mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
            }

            @Override
            public void onFailure(int arg0) {
                emitter.onError(new Exception(String.valueOf(arg0)));
            }
        }));
    }

    private Single<Squad> removeService(Squad squad, WifiP2pServiceRequest serviceRequest) {
        return Single.create(emitter -> mManager.removeServiceRequest(mChannel, serviceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                emitter.onSuccess(squad);
            }

            @Override
            public void onFailure(int arg0) {
                emitter.onError(new Exception(String.valueOf(arg0)));
            }
        }));
    }

    private Completable connectService(Squad device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.leaderLocation();
        config.wps.setup = WpsInfo.PBC;
        return Completable.create(emitter -> mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                emitter.onComplete();
            }

            @Override
            public void onFailure(int errorCode) {
                emitter.onError(new Exception(String.valueOf(errorCode)));
            }
        }));
    }

    @Override
    public Single<Squad> joinSquad(Squad squad) {
        return connectService(squad).andThen(Single.just(squad));
    }

    public WifiP2PReceiver getReceiver() {
        return receiver;
    }
}
