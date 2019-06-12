package com.wharfofwisdom.focusmediaplayer.domain.repository.db;

import android.content.Context;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.dao.AdvertisementDao;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.entity.AdWithVideo;
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
    public Flowable<List<Advertisement>> getNotDownloadAdvertisement(final List<Advertisement> advertisements) {
        return advertisementDao.getAdvertisements().map(videoEntities -> {
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

    @Override
    public Flowable<List<Advertisement>> getDownloadedAdvertisementAndVideo(String[] advertisementIds) {
        return advertisementDao.getAdvertisements(advertisementIds).map(videoEntities -> {
            List<Advertisement> videos = AdvertisementMapper.convert(videoEntities);
            Iterator<Advertisement> iterator = videos.iterator();
            while (iterator.hasNext()) {
                Advertisement advertisement = iterator.next();
                if (!isVideoExist(advertisement, videoEntities)) {
                    iterator.remove();
                }
            }
            return videos;
        });
    }

    private boolean isVideoExist(Advertisement advertisement, List<AdWithVideo> adWithVideos) {
        for (AdWithVideo adWithVideo : adWithVideos) {
            if (advertisement.id().equals(adWithVideo.id) && adWithVideo.videos.size() > 0) {
                File file = new File(adWithVideo.videos.get(0).filePath);
                return file.exists() && file.isFile();
            }
        }
        return false;
    }
}
