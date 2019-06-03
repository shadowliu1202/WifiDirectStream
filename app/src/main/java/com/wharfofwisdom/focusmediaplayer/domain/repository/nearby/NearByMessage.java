package com.wharfofwisdom.focusmediaplayer.domain.repository.nearby;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.message.Message;

import java.io.File;

public class NearByMessage extends Message {
    private final String message;
    private final File file;

    NearByMessage(String message) {
        this.message = message;
        file = null;
    }

    public NearByMessage(File file) {
        this.file = file;
        message = file.getAbsolutePath();
    }

    @Override
    public String message() {
        return message;
    }
}
