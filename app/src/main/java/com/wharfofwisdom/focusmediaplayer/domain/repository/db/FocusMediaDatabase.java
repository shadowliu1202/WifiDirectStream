package com.wharfofwisdom.focusmediaplayer.domain.repository.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao.AdvertisementDao;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdEntity;

@Database(entities = {AdEntity.class}, version = 1, exportSchema = false)
public abstract class FocusMediaDatabase extends RoomDatabase {
    private static FocusMediaDatabase INSTANCE;

    public static FocusMediaDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FocusMediaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FocusMediaDatabase.class, "LuckyPaDatabase")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AdvertisementDao advertisementDao();
}
