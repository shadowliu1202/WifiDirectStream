package com.wharfofwisdom.focusmediaplayer.domain.model.squad.position;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Squad implements Parcelable {

    public static final Squad NO_SQUAD = Squad.builder().leaderLocation("n/a").name("n/a").build();

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

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Squad && ((Squad) obj).name().equals(name());
    }
}
