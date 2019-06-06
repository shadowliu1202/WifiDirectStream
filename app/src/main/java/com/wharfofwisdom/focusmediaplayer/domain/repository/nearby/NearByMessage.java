package com.wharfofwisdom.focusmediaplayer.domain.repository.nearby;

import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;

import java.io.File;

public class NearByMessage extends Mission {
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

    @Override
    public String mission() {
        return getClass().getSimpleName();
    }
}
