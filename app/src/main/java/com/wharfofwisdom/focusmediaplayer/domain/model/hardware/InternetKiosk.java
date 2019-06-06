package com.wharfofwisdom.focusmediaplayer.domain.model.hardware;

/**
 * InternetKiosk : Consider as a Kiosk has Internet connection.
 */
public final class InternetKiosk extends Kiosk {
    private static final InternetKiosk instance = new InternetKiosk();
    private String mac;

    public static InternetKiosk createInstance(String mac) {
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
