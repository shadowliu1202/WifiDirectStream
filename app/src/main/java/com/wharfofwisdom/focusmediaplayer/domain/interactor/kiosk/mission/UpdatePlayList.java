package com.wharfofwisdom.focusmediaplayer.domain.interactor.kiosk.mission;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wharfofwisdom.focusmediaplayer.domain.model.Advertisement;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.mission.Mission;
import com.wharfofwisdom.focusmediaplayer.presentation.helper.MyAdapterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UpdatePlayList extends Mission {
    private final List<Advertisement> advertisements;

    public UpdatePlayList(List<Advertisement> advertisements) {
        this.advertisements = advertisements;
    }

    public UpdatePlayList(String json) {
        Log.d("Test", "Get:" + json);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MyAdapterFactory.create())
                .create();
        Type type = new TypeToken<ArrayList<Advertisement>>() {
        }.getType();
        advertisements = gson.fromJson(json, type);
        Log.d("Test", "Get:" + advertisements.size());
    }

    @Override
    public String message() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MyAdapterFactory.create())
                .create();
        String string = gson.toJson(advertisements);
        Log.d("Test","convert To:"+string);
        return string;
    }

    @Override
    public String mission() {
        return getClass().getSimpleName();
    }
}
