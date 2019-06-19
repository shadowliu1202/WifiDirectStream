package com.wharfofwisdom.focusmediaplayer.di;

import com.wharfofwisdom.focusmediaplayer.InitialActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = MainActivityModule.class)
public interface InitialActivityComponent extends AndroidInjector<InitialActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<InitialActivity> {
    }
}
