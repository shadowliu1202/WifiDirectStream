package com.wharfofwisdom.focusmediaplayer.di;

import com.wharfofwisdom.focusmediaplayer.InitialActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
@SuppressWarnings("unused")
abstract class ActivityBindingModule {
    @ContributesAndroidInjector(modules = {InitialActivityModule.class})
    abstract InitialActivity initialActivity();
}