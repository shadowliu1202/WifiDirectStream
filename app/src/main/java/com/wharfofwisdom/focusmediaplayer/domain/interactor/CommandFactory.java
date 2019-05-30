package com.wharfofwisdom.focusmediaplayer.domain.interactor;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Signaller;

public class CommandFactory {
    public static Signaller createSignaller(boolean hasSquad) {
        return Signaller.createInstance();
    }
}
