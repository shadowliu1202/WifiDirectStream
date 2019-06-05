package com.wharfofwisdom.focusmediaplayer.domain.repository.db;

import android.content.Context;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao.AdvertisementDao;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.VideoEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.mapper.AdvertisementMapper;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RoomRepository implements CacheRepository {
    private final AdvertisementDao advertisementDao;

    public RoomRepository(Context context) {
        advertisementDao = FocusMediaDatabase.getDatabase(context).advertisementDao();

        advertisementDao.getVideos().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<VideoEntity>>() {
                    @Override
                    public void accept(List<VideoEntity> videoEntities) throws Exception {
                        Log.d("test", "get:" + videoEntities.size());
                    }
                }, Throwable::printStackTrace);

    }

    @Override
    public Flowable<List<Advertisement>> getAdvertisements() {
        return advertisementDao.getAdvertisements().map(AdvertisementMapper::convert);
    }

    @Override
    public Completable addVideoCache(File file, String advertisementId, String videoId) {
        return advertisementDao.saveVideo(VideoEntity.VideoEntityBuilder.aVideoEntity()
                .withFilePath(file.getPath()).withAdId(advertisementId)
                .withId(videoId).build());
    }

    @Override
    public Completable setAdvertisements(List<Advertisement> advertisements) {
        return advertisementDao.saveAdvertisements(AdvertisementMapper.convertToEntity(advertisements));
    }
}
