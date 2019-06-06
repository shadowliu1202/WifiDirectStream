package com.wharfofwisdom.focusmediaplayer.domain.executor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.InternetKiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.WirelessKiosk;

public class KioskFactory {
    public static Kiosk create(Context context) {
        //TODO : Temp use to differ Signaller/Soldier
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            return InternetKiosk.createInstance(getLocalMacAddressFromWifiInfo(context));
        }
        return new WirelessKiosk();
    }

    @SuppressLint("HardwareIds")
    private static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifi.getConnectionInfo();
        return TextUtils.isEmpty(wInfo.getMacAddress()) ? "測試:團隊" : wInfo.getMacAddress();
    }

}
