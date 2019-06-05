package com.wharfofwisdom.focusmediaplayer.domain.model.hardware;

/**
 * Solider : Consider as a Kiosk has Internet connection.
 */
public final class NetworkKiosk extends Kiosk {
    private static final NetworkKiosk instance = new NetworkKiosk();
    private String mac;

    public static NetworkKiosk createInstance(String mac) {
        instance.mac = parseLastMac(mac);
        return instance;
    }

    private static String parseLastMac(String mac) {
        String[] strings = mac.split(":");
        if (strings.length >= 2) {
            return strings[strings.length - 2] + strings[strings.length - 1];
        }
        return "";
    }

    @Override
    public boolean hasInternet() {
        return true;
    }

    @Override
    public String name() {
        return mac;
    }
}
