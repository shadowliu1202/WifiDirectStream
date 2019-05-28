package com.wharfofwisdom.focusmediaplayer.domain.model;

import android.net.Uri;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Advertisement {
    public abstract String id();

    public abstract String name();

    public abstract Uri url();

    public static Builder builder() {
        return new AutoValue_Advertisement.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder name(String name);

        public abstract Builder url(Uri url);

        public abstract Advertisement build();
    }
}
