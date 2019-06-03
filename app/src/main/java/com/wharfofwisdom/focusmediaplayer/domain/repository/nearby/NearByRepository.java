package com.wharfofwisdom.focusmediaplayer.domain.repository.nearby;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.message.Message;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subjects.ReplaySubject;

public class NearByRepository implements SquadRepository {
    private final Context context;
    private final ReplaySubject<Message> commands = ReplaySubject.create();

    public NearByRepository(Context context) {
        this.context = context;
    }

    @Override
    public Flowable<Message> waitCommand() {
        return commands.toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Single<Squad> announceSquad(Squad squadName) {
        return Single.create(emitter -> Nearby.getConnectionsClient(context)
                .startAdvertising(
                        /* endpointName= */ "Device A",
                        /* serviceId= */ "com.example.package_name",
                        new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                                emitter.onSuccess(Squad.builder().name(endpointId).leaderLocation(endpointId).build());
                                Nearby.getConnectionsClient(context).acceptConnection(endpointId, new PayloadCallback() {
                                    @Override
                                    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                        byte[] receivedBytes = payload.asBytes();
                                        String message = new String(receivedBytes);
                                        Log.d("Test",s+":"+message);
                                        commands.onNext(new NearByMessage(message));
                                    }

                                    @Override
                                    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate update) {
                                        Log.d("Test", "onPayloadTransferUpdate" + update.getStatus());
                                    }
                                });
                            }

                            @Override
                            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
                                if (connectionResolution.getStatus().getStatusCode() != ConnectionsStatusCodes.STATUS_OK) {
                                    commands.onError(new Exception(endpointId + ":" + connectionResolution.getStatus().getStatusCode()));
                                }
                            }

                            @Override
                            public void onDisconnected(@NonNull String s) {
                                //commands.onError(new Exception(s));
                            }
                        },
                        new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build())
                .addOnFailureListener(emitter::onError));
    }

    @Override
    public Single<Squad> searchSquad(String squadName) {
        return null;
    }

    @Override
    public Single<Squad> createSquad(Soldier soldier) {
        return Single.just(Squad.builder().leaderLocation("").name(soldier.getSquadName()).build());
    }

    @Override
    public Single<Squad> searchSquad() {
        return Single.create(emitter -> {
            DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
            Nearby.getConnectionsClient(context).startDiscovery("com.example.package_name", new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // An endpoint was found. We request a connection to it.
                    emitter.onSuccess(Squad.builder().name(endpointId).leaderLocation(endpointId).build());
                }

                @Override
                public void onEndpointLost(@NonNull String s) {
                    Log.d("test", "onEndpointLost");
                }
            }, discoveryOptions)
                    .addOnFailureListener(emitter::onError);
        });
    }

    @Override
    public Single<Squad> joinSquad(Squad squad) {
        return Single.create(emitter -> Nearby.getConnectionsClient(context)
                .requestConnection("test", squad.leaderLocation(), new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                        Nearby.getConnectionsClient(context).acceptConnection(endpointId, new PayloadCallback() {
                            @Override
                            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                byte[] receivedBytes = payload.asBytes();
                                commands.onNext(new NearByMessage(new String(receivedBytes)));
                            }

                            @Override
                            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate update) {
                                Log.d("Test", "onPayloadTransferUpdate" + update.getStatus());
                            }
                        });
                    }

                    @Override
                    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
                        if (connectionResolution.getStatus().getStatusCode() != ConnectionsStatusCodes.STATUS_OK) {
                            commands.onError(new Exception(endpointId + ":" + connectionResolution.getStatus().getStatusCode()));
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull String s) {
                        commands.onError(new Exception(s));
                    }
                })
                .addOnSuccessListener((Void unused) -> emitter.onSuccess(squad))
                .addOnFailureListener(emitter::onError));
    }
}
