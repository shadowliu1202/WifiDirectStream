package com.wharfofwisdom.focusmediaplayer.di;


import android.app.Application;

import com.wharfofwisdom.focusmediaplayer.domain.executor.KioskFactory;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ViewModelModule.class})
class AppModule {
    @Provides
    @Singleton
    Kiosk provideKiosk(Application application) {
        return KioskFactory.create(application);
    }

}
