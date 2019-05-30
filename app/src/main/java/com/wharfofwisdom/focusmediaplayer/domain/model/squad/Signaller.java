package com.wharfofwisdom.focusmediaplayer.domain.model.squad;

public final class Signaller extends Soldier {
    private static final Signaller instance = new Signaller();

    public static Signaller createInstance() {
        return instance;
    }


}
