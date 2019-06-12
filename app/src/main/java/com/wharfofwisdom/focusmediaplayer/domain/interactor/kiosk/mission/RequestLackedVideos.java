package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.presentation.helper.MyAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RequestLackedVideos extends Mission {

    public List<Advertisement> getAdvertisements() {
        return advertisements;
    }

    private final List<Advertisement> advertisements;

    public RequestLackedVideos(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    public RequestLackedVideos(String json) {
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
        return "RequestLackedVideos";
    }
}
