package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Signaller;

public class CommandFactory {
    public static Signaller createSignaller(boolean hasSquad, Context context) {
        return Signaller.createInstance(parseLastMac(getLocalMacAddressFromWifiInfo(context)));
    }

    private static String parseLastMac(String mac) {
        String[] strings = mac.split(":");
        if (strings.length >= 2) {
            return strings[strings.length - 2] + strings[strings.length - 1];
        }
        return "";
    }

    @SuppressLint("HardwareIds")
    private static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo winfo = wifi.getConnectionInfo();
        return TextUtils.isEmpty(winfo.getMacAddress()) ? "測試:團隊" : winfo.getMacAddress();
    }
}
