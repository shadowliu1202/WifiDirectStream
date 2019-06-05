package com.wharfofwisdom.focusmediaplayer.presentation.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WifiP2PReceiver extends BroadcastReceiver {

    private final WifiP2pManager.Channel mChannel;
    private final WifiP2pManager mManager;
    private final EventListener listener;
    private List<WifiP2pDevice> peers = new ArrayList<>();

    public interface EventListener {
        void onErrors(Throwable throwable);

        void onPeersChanged(List<WifiP2pDevice> peers);

        void onConnected(WifiP2pInfo p2pInfo, NetworkInfo networkInfo);

        void onDisconnected();

        void onInformation(String string);
    }

    public WifiP2PReceiver(WifiP2pManager.Channel mChannel, WifiP2pManager mManager, EventListener listener) {
        this.mChannel = mChannel;
        this.mManager = mManager;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.d("WifiP2PReceiver", "WIFI_P2P_STATE_CHANGED_ACTION");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                listener.onErrors(new Exception("Not Support P2p"));
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("WifiP2PReceiver", "WIFI_P2P_PEERS_CHANGED_ACTION");
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerList -> {
                    Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
                    if (!refreshedPeers.equals(peers)) {
                        peers.clear();
                        peers.addAll(refreshedPeers);
                        listener.onPeersChanged(peers);
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.d("WifiP2PReceiver", "WIFI_P2P_CONNECTION_CHANGED_ACTION");
            WifiP2pInfo p2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                listener.onConnected(p2pInfo, networkInfo);
            } else {
                listener.onDisconnected();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("WifiP2PReceiver", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
            listener.onInformation("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }

//    public void sendMessage(Context context, boolean isOwner, InetAddress ownerAddress) {
////		Log.v(TAG, "Send message starts");
//        // Message written in EditText is always sent
//        //Message mes = new Message(type, edit.getText().toString(), null, FullscreenActivity.chatName);
//
////        switch (type) {
////            case Message.IMAGE_MESSAGE:
////                Image image = new Image(this, fileUri);
////                Log.e(TAG, "Bitmap from url ok" + fileUri);
////                mes.setByteArray(image.bitmapToByteArray(image.getBitmapFromUri()));
////                mes.setFileName(image.getFileName());
////                mes.setFileSize(image.getFileSize());
////                Log.e(TAG, "Set byte array to image ok" + image.getFileSize() + "-" + image.getFileName());
////
////                break;
////            case Message.AUDIO_MESSAGE:
////                MediaFile audioFile = new MediaFile(this, fileURL, Message.AUDIO_MESSAGE);
////                mes.setByteArray(audioFile.fileToByteArray());
////                mes.setFileName(audioFile.getFileName());
////                mes.setFilePath(audioFile.getFilePath());
////                break;
////            case Message.VIDEO_MESSAGE:
////                MediaFile videoFile = new MediaFile(this, fileURL, Message.AUDIO_MESSAGE);
////                mes.setByteArray(videoFile.fileToByteArray());
////                mes.setFileName(videoFile.getFileName());
////                mes.setFilePath(videoFile.getFilePath());
////                tmpFilesUri.add(fileUri);
////                break;
////            case Message.FILE_MESSAGE:
////                MediaFile file = new MediaFile(this, fileURL, Message.FILE_MESSAGE);
////                mes.setByteArray(file.fileToByteArray());
////                mes.setFileName(file.getFileName());
////                break;
////            case Message.DRAWING_MESSAGE:
////                MediaFile drawingFile = new MediaFile(this, fileURL, Message.DRAWING_MESSAGE);
////                mes.setByteArray(drawingFile.fileToByteArray());
////                mes.setFileName(drawingFile.getFileName());
////                mes.setFilePath(drawingFile.getFilePath());
////                break;
////        }
////		Log.e(TAG, "Message object hydrated");
//
//        // First cycle in tracking where this msg goes.
//        // MARK: 16/06/2018 Once msg instantiated, get and records user chat name.
////        if (isOwner) {
////            Message mes = new Message(Message.TEXT_MESSAGE, "Welcome", null, "Owner");
////            mes.setUser_record("Owner");
////            Log.e("Test", "Message hydrated, start SendMessageServer AsyncTask");
////
////            new SendMessageServer(context, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
////        } else {
////            Message mes = new Message(Message.TEXT_MESSAGE, "Banjo", null, "Client");
////            mes.setUser_record("Client");
////            new SendMessageClient(context, ownerAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
////            Log.e("Test", "Message hydrated, start SendMessageClient AsyncTask");
////        }
//
//
////		Log.e(TAG, "Start AsyncTasks to send the message");
////
////        if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_OWNER) {
////            Log.e(TAG, "Message hydrated, start SendMessageServer AsyncTask");
////
////            new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
////        } else if (mReceiver.isGroupeOwner() == WifiDirectBroadcastReceiver.IS_CLIENT) {
////            Log.e(TAG, "Message hydrated, start SendMessageClient AsyncTask");
////
////            new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
//////        }
////        Log.d("Test", "isConnected : " + networkInfo.toString());
////        Log.d("Test", "isConnected : " + p2pInfo.toString());
////        mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
////            @Override
////            public void onPeersAvailable(WifiP2pDeviceList peers) {
////                for (WifiP2pDevice wifiP2pDevice : peers.getDeviceList()) {
////                    Log.d("Test", "onPeersAvailable :" + wifiP2pDevice.deviceName);
////                }
////
////            }
////        });
////        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
////            @Override
////            public void onConnectionInfoAvailable(WifiP2pInfo info) {
////                Log.d("Test", "isConnected    " + info.toString());
////                if (info.groupFormed && info.isGroupOwner) {
////                    ServerInit server = new ServerInit();
////                    server.start();
////                    //sendMessage(context, true, info.groupOwnerAddress);
////                } else if (info.groupFormed) {
////                    ClientInit client = new ClientInit(info.groupOwnerAddress);
////                    client.start();
////                    sendMessage(context, false, info.groupOwnerAddress);
////                }
////            }
////        });
//    }
}
