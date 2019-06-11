package com.wharfofwisdom.focusmediaplayer.domain.repository.db;

import android.content.Context;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.Video;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao.AdvertisementDao;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.VideoEntity;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.mapper.AdvertisementMapper;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class RoomRepository implements CacheRepository, AdvertisementRepository {
    private final AdvertisementDao advertisementDao;

    public RoomRepository(Context context) {
        advertisementDao = FocusMediaDatabase.getDatabase(context).advertisementDao();
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

    @Override
    public Flowable<List<Video>> getNotDownloadVideo(List<Advertisement> advertisements) {
        String[] ads = new String[advertisements.size()];
        for (int i = 0; i < advertisements.size(); i++) {
            ads[i] = advertisements.get(i).id();
        }
        return advertisementDao.getNotDownloadedVideos(ads).map(AdvertisementMapper::convertVideos);
    }

    @Override
    public Flowable<List<Advertisement>> getNotDownloadAdvertisement(final List<Advertisement> advertisements) {
        String[] ads = new String[advertisements.size()];
        for (int i = 0; i < advertisements.size(); i++) {
            ads[i] = advertisements.get(i).id();
        }
        return advertisementDao.getNotDownloadedVideos(ads).map(videoEntities -> {
            Iterator<Advertisement> iterator = advertisements.iterator();
            while (iterator.hasNext()) {
                Advertisement advertisement = iterator.next();
                if (isVideoExist(advertisement, videoEntities)) {
                    iterator.remove();
                }
            }
            return advertisements;
        });
    }

    private boolean isVideoExist(Advertisement advertisement, List<VideoEntity> videoEntities) {
        for (VideoEntity videoEntity : videoEntities) {
            if (advertisement.id().equals(videoEntity.adId)) {
                File file = new File(videoEntity.filePath);
                return file.exists() && file.isFile();
            }
        }
        return false;
    }
}
