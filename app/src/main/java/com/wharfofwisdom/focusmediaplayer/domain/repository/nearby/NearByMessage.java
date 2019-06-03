package com.wharfofwisdom.focusmediaplayer.domain.repository.nearby;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.message.Message;

public class NearByMessage extends Message {
    private final String message;

    NearByMessage(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
