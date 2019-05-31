package com.wharfofwisdom.focusmediaplayer.domain.model.squad;

public final class Signaller extends Soldier {
    private static final Signaller instance = new Signaller();
    private String defaultSquadName;

    public static Signaller createInstance(String defaultSquadName) {
        instance.defaultSquadName = defaultSquadName;
        return instance;
    }

    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public String getSquadName() {
        return defaultSquadName;
    }
}
