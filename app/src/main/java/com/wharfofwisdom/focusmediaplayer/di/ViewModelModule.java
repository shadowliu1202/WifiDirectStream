package com.wharfofwisdom.focusmediaplayer.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wharfofwisdom.focusmediaplayer.FindSquadViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
@SuppressWarnings("unused")
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FindSquadViewModel.class)
    abstract ViewModel bindFindSquadViewModel(FindSquadViewModel findSquadViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory appViewModelFactory);

}
