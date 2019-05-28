package com.wharfofwisdom.focusmediaplayer.domain.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Video {
    public abstract String id();

    public abstract String name();

    public abstract Uri url();

    public abstract int index();

    public static Builder builder() {
        return new AutoValue_Video.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder name(String name);

        public abstract Builder url(Uri url);

        public abstract Builder index(int index);

        public abstract Video build();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Video && ((Video) obj).id().equals(id());
    }
}
