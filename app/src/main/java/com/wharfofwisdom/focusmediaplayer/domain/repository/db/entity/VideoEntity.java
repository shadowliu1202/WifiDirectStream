package com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class VideoEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    @NonNull
    public String adId = "";
    @Nullable
    public String filePath;

    public static final class VideoEntityBuilder {
        public String id = "";
        String adId = "";
        String filePath;

        private VideoEntityBuilder() {
        }

        public static VideoEntityBuilder aVideoEntity() {
            return new VideoEntityBuilder();
        }

        public VideoEntityBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public VideoEntityBuilder withAdId(String adId) {
            this.adId = adId;
            return this;
        }

        public VideoEntityBuilder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public VideoEntity build() {
            VideoEntity videoEntity = new VideoEntity();
            videoEntity.filePath = this.filePath;
            videoEntity.id = this.id;
            videoEntity.adId = this.adId;
            return videoEntity;
        }
    }
}
