package com.wharfofwisdom.focusmediaplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.wharfofwisdom.focusmediaplayer.databinding.ActivityWelcomeBinding;
import com.wharfofwisdom.focusmediaplayer.domain.executor.KioskFactory;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.AdvertisementActivity;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class InitialActivity extends DaggerAppCompatActivity {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
//    private WifiP2PReceiver receiver;
    private final IntentFilter intentFilter = new IntentFilter();
//    @Inject
    ViewModelProvider.Factory factory;
    FindSquadViewModel findSquadViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
//        Kiosk soldier = KioskFactory.create(this);
//        P2PRepository repository = initP2P();
//        receiver = repository.getReceiver();
//        FindSquadViewModel findSquadViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
//            @NonNull
//            @Override
//            @SuppressWarnings("unchecked")
//            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                return (T) new FindSquadViewModel(soldier, repository);
//            }
//        }).get(FindSquadViewModel.class);
        findSquadViewModel = ViewModelProviders.of(this, factory).get(FindSquadViewModel.class);
        binding.setViewModel(findSquadViewModel);
        binding.setLifecycleOwner(this);
        hideSystemUI();
        compositeDisposable.add(findSquadViewModel.initializeSquad()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startAdvertisement, Throwable::printStackTrace));
    }

    //TODO
    private P2PRepository initP2P() {
        WifiP2pManager mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel mChannel = mManager.initialize(this, getMainLooper(), null);
        return new P2PRepository(mManager, mChannel);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(receiver, intentFilter);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(receiver);
//    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void startAdvertisement(Squad.POSITION position) {
        //TODO : Test
        Toast.makeText(this, "Start Squad as :" + position.name(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AdvertisementActivity.class);
        startActivity(intent.putExtra(AdvertisementActivity.SQUAD, position));
        finish();
    }
}
