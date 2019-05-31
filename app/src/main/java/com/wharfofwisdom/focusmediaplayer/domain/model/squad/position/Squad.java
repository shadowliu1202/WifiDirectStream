package com.wharfofwisdom.focusmediaplayer.domain.model.squad.position;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Squad {
    public abstract String name();

    public static Builder builder() {
        return new AutoValue_Squad.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Squad build();
    }
}
