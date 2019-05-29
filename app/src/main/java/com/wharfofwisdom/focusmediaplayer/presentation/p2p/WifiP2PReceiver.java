package com.wharfofwisdom.focusmediaplayer.presentation.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WifiP2PReceiver extends BroadcastReceiver {

    private final WifiP2pManager.Channel mChannel;
    private final WifiP2pManager mManager;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public WifiP2PReceiver(WifiP2pManager.Channel mChannel, WifiP2pManager mManager) {
        this.mChannel = mChannel;
        this.mManager = mManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Log.d("Test", "WIFI_P2P_STATE_CHANGED_ACTION:" + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("Test", "WIFI_P2P_PEERS_CHANGED_ACTION:");
            // The peer list has changed! We should probably do something about
            // that.
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.
            Log.d("Test", "WIFI_P2P_CONNECTION_CHANGED_ACTION:");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            Log.d("Test", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:");
        }
    }


    private WifiP2pManager.PeerListListener peerListListener = peerList -> {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        Log.d("Test", "Get Peers:" + refreshedPeers.size());
        if (!refreshedPeers.equals(peers)) {
            peers.clear();
            peers.addAll(refreshedPeers);
        }
    };
}
