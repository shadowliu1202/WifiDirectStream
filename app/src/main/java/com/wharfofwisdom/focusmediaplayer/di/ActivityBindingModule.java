package com.wharfofwisdom.focusmediaplayer.di;

import com.wharfofwisdom.focusmediaplayer.InitialActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
@SuppressWarnings("unused")
public abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = {InitialActivityModule.class})
    public abstract InitialActivity initialActivity();
}