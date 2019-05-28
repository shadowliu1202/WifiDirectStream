package com.wharfofwisdom.focusmediaplayer.domain.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Advertisement {
    public abstract String id();

    public abstract int index();

    public abstract Video video();

    public static Builder builder() {
        return new AutoValue_Advertisement.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder video(Video video);

        public abstract Builder index(int index);

        public abstract Advertisement build();
    }
}
