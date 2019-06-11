package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CacheRepository;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.presentation.helper.MyAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class UpdatePlayList extends Mission {
    private final List<Advertisement> advertisements;

    public UpdatePlayList(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    public UpdatePlayList(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MyAdapterFactory.create())
                .create();
        Type type = new TypeToken<ArrayList<Advertisement>>() {
        }.getType();
        advertisements = gson.fromJson(json, type);
    }

    @Override
    public String message() {
        return new Gson().toJson(advertisements);
    }

    @Override
    public String mission() {
        return getClass().getSimpleName();
    }


    public Completable execute(CacheRepository repository) {
        return repository.setAdvertisements(advertisements).subscribeOn(Schedulers.io());
    }
}
