package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk;

import android.content.Context;

import com.wharfofwisdom.focusmediaplayer.BuildConfig;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.playlist.BuildingService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class KioskClient {
    private static final String BASE_URL = "https://api.focusmedia.cloud/";
    private static final int TIME_OUT = 10;
    private static OkHttpClient mOkHttpClient;
    private Retrofit retrofit;

    public KioskClient(Context context) {
        initOkHttpClient(context);
        initRetrofit();
    }

    private void initOkHttpClient(Context context) {
        if (mOkHttpClient == null) {
            synchronized (KioskClient.class) {
                if (mOkHttpClient == null) {
                    Cache cache = new Cache(new File(context.getCacheDir(), "Cache"), 1024 * 1024 * 100);
                    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .cache(cache)
                            .retryOnConnectionFailure(true);
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        okHttpClientBuilder.addInterceptor(logging);
                    }
                    mOkHttpClient = okHttpClientBuilder.build();
                }
            }
        }
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public BuildingService getBuildingService() {
        return retrofit.create(BuildingService.class);
    }
}
