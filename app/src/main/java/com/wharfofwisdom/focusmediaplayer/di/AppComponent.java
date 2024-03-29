package com.wharfofwisdom.focusmediaplayer.di;

import android.app.Application;

import com.wharfofwisdom.focusmediaplayer.FocusMediaApplication;
import javax.inject.Singleton;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ActivityBindingModule.class, AppModule.class})
public interface AppComponent extends AndroidInjector<FocusMediaApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}