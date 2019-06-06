package com.wharfofwisdom.focusmediaplayer.domain.repository.nearby;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import java.io.File;
import java.nio.charset.StandardCharsets;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.subjects.ReplaySubject;


public class NearByRepository implements SquadRepository {

    private final ReplaySubject<Mission> commands = ReplaySubject.create();
    private static NearByRepository nearByRepository = null;
    private final ConnectionsClient connectionsClient;
    private final SimpleArrayMap<Long, Payload> incomingFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Payload> completedFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();

    public static NearByRepository createInstance(Context context) {
        if (nearByRepository == null) {
            nearByRepository = new NearByRepository(context.getApplicationContext());
        }
        return nearByRepository;
    }

    private NearByRepository(Context context) {
        connectionsClient = Nearby.getConnectionsClient(context);
    }

    @Override
    public Completable request(Mission mission) {
        return null;
    }

    @Override
    public Flowable<Mission> waitCommand() {
        return commands.toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Single<Squad> announceSquad(Squad squadName) {
        return Single.create(emitter -> connectionsClient
                .startAdvertising(
                        /* endpointName= */ "Device A",
                        /* serviceId= */ "com.example.package_name",
                        new ConnectionLifecycleCallback() {
                            @Override
                            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                                emitter.onSuccess(Squad.builder().name(endpointId).address(endpointId).build());
                                connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                                    @Override
                                    public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                        if (payload.getType() == Payload.Type.BYTES) {
                                            String payloadFilenameMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
                                            Log.d("Test", s + ":" + payloadFilenameMessage);
                                            commands.onNext(new NearByMessage(payloadFilenameMessage));
                                            long payloadId = addPayloadFilename(payloadFilenameMessage);
                                            processFilePayload(payloadId);
                                        } else if (payload.getType() == Payload.Type.FILE) {
                                            // Add this to our tracking map, so that we can retrieve the payload later.
                                            incomingFilePayloads.put(payload.getId(), payload);
                                        }
                                    }

                                    @Override
                                    public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate update) {
                                        if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                                            long payloadId = update.getPayloadId();
                                            Payload payload = incomingFilePayloads.remove(payloadId);
                                            completedFilePayloads.put(payloadId, payload);
                                            if (payload != null && payload.getType() == Payload.Type.FILE) {
                                                File file = processFilePayload(payloadId);
                                                commands.onNext(new NearByMessage(file));
                                            }
                                        }
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
                        new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
                .addOnFailureListener(emitter::onError));
    }

    /**
     * Extracts the payloadId and filename from the message and stores it in the
     * filePayloadFilenames map. The format is payloadId:filename.
     */
    private long addPayloadFilename(String payloadFilenameMessage) {
        String[] parts = payloadFilenameMessage.split(":");
        long payloadId = Long.parseLong(parts[0]);
        String filename = parts[1];
        filePayloadFilenames.put(payloadId, filename);
        return payloadId;
    }

    private File processFilePayload(long payloadId) {
        // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
        // payload is completely received. The file payload is considered complete only when both have
        // been received.
        Payload filePayload = completedFilePayloads.get(payloadId);
        String filename = filePayloadFilenames.get(payloadId);
        if (filePayload != null && filename != null) {
            completedFilePayloads.remove(payloadId);
            filePayloadFilenames.remove(payloadId);

            // Get the received file (which will be in the Downloads folder)
            File payloadFile = filePayload.asFile().asJavaFile();

            // Rename the file.
            payloadFile.renameTo(new File(payloadFile.getParentFile(), filename));
            return payloadFile;
        }
        return null;
    }


    @Override
    public Single<Squad> searchSquad(String squadName) {
        return null;
    }

    @Override
    public Single<Squad> createSquad(Kiosk soldier) {
        return Single.just(Squad.builder().address("").name(soldier.name()).build());
    }

    @Override
    public Single<Squad> searchSquad() {
        return Single.create(emitter -> {
            DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
            connectionsClient.startDiscovery("com.example.package_name", new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // An endpoint was found. We request a connection to it.
                    emitter.onSuccess(Squad.builder().name(endpointId).address(endpointId).build());
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
    public Single<Squad.POSITION> joinSquad(Squad squad) {
        return Single.create(emitter -> connectionsClient
                .requestConnection("test", squad.address(), new ConnectionLifecycleCallback() {
                    @Override
                    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                        connectionsClient.acceptConnection(endpointId, new PayloadCallback() {
                            @Override
                            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                                if (payload.getType() == Payload.Type.BYTES) {
                                    String payloadFilenameMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
                                    Log.d("Test", s + ":" + payloadFilenameMessage);
                                    commands.onNext(new NearByMessage(payloadFilenameMessage));
                                    long payloadId = addPayloadFilename(payloadFilenameMessage);
                                    processFilePayload(payloadId);
                                } else if (payload.getType() == Payload.Type.FILE) {
                                    Log.d("Test", s + ":" + "Payload.Type.FILE");
                                    // Add this to our tracking map, so that we can retrieve the payload later.
                                    incomingFilePayloads.put(payload.getId(), payload);
                                }
                            }

                            @Override
                            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate update) {
                                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                                    Log.d("test", "PayloadTransferUpdate.Status.SUCCESS");
                                    long payloadId = update.getPayloadId();
                                    Payload payload = incomingFilePayloads.remove(payloadId);
                                    completedFilePayloads.put(payloadId, payload);
                                    if (payload != null && payload.getType() == Payload.Type.FILE) {
                                        File file = processFilePayload(payloadId);
                                        commands.onNext(new NearByMessage(file));
                                    }
                                }
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
                .addOnSuccessListener((Void unused) -> emitter.onSuccess(Squad.POSITION.Follower))
                .addOnFailureListener(emitter::onError));
    }
}
