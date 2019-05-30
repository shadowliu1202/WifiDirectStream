package com.wharfofwisdom.focusmediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.wharfofwisdom.focusmediaplayer.databinding.ActivityWelcomeBinding;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CommandFactory;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.presentation.FullscreenActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class WelcomeActivity extends AppCompatActivity {

    private Soldier soldier = CommandFactory.createSignaller(false);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        FindSquadViewModel findSquadViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FindSquadViewModel(soldier);
            }
        }).get(FindSquadViewModel.class);
        binding.setViewModel(findSquadViewModel);
        binding.setLifecycleOwner(this);
        hideSystemUI();
        compositeDisposable.add(findSquadViewModel.decideSquad()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startPlay, Throwable::printStackTrace));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void startPlay(Squad squad) {
        startActivity(new Intent(this, FullscreenActivity.class));
        finish();
    }
}
