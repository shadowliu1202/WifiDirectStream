package com.wharfofwisdom.focusmediaplayer.domain.model.squad.position;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Squad implements Parcelable {
    public abstract String name();

    public abstract String leaderLocation();

    public static Builder builder() {
        return new AutoValue_Squad.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Builder leaderLocation(String leaderLocation);

        public abstract Squad build();
    }
}
