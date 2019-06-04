package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AdEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    public int order;
    public String videoId;
    public String videoUrl;

    public static final class AdEntityBuilder {
        public String id = "";
        public int order;
        public String videoId;
        public String videoUrl;

        private AdEntityBuilder() {
        }

        public static AdEntityBuilder anAdEntity() {
            return new AdEntityBuilder();
        }

        public AdEntityBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public AdEntityBuilder withOrder(int order) {
            this.order = order;
            return this;
        }

        public AdEntityBuilder withVideoId(String videoId) {
            this.videoId = videoId;
            return this;
        }

        public AdEntityBuilder withVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public AdEntity build() {
            AdEntity adEntity = new AdEntity();
            adEntity.order = this.order;
            adEntity.id = this.id;
            adEntity.videoId = this.videoId;
            adEntity.videoUrl = this.videoUrl;
            return adEntity;
        }
    }
}
