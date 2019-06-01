package com.wharfofwisdom.focusmediaplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.wharfofwisdom.focusmediaplayer.databinding.ActivityWelcomeBinding;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageClient;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.SendMessageServer;
import com.wharfofwisdom.focusmediaplayer.demo.ClientInit;
import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.demo.MessageService;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.CommandFactory;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Signaller;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;
import com.wharfofwisdom.focusmediaplayer.presentation.p2p.WifiP2PReceiver;

import java.net.InetAddress;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class WelcomeActivity extends AppCompatActivity {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Soldier soldier;
    private WifiP2PReceiver receiver;
    private P2PRepository repository;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        soldier = CommandFactory.createSolider(false, this);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        repository = new P2PRepository(mManager, mChannel);
        receiver = repository.getReceiver();
        FindSquadViewModel findSquadViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FindSquadViewModel(soldier, repository);
            }
        }).get(FindSquadViewModel.class);
        binding.setViewModel(findSquadViewModel);
        binding.setLifecycleOwner(this);
        hideSystemUI();
        compositeDisposable.add(findSquadViewModel.initializedSquad()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::startPlay, Throwable::printStackTrace));
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
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
        Toast.makeText(this, "Find Squad" + squad.name(), Toast.LENGTH_LONG).show();
//        startActivity(new Intent(this, FullscreenActivity.class));
//        finish();
        startService(new Intent(this, MessageService.class));
        mManager.requestConnectionInfo(mChannel, info -> sendMessage(info.groupOwnerAddress));
    }

    public void sendMessage(InetAddress ownerAddress) {
        if (soldier instanceof Signaller) {
            ServerInit server = new ServerInit();
            server.start();
            Message mes = new Message(Message.TEXT_MESSAGE, "Welcome", null, "Owner");
            mes.setUser_record("Owner");
            Log.e("Test", "Message hydrated, start SendMessageServer AsyncTask");
            new SendMessageServer(this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
        } else {
            ClientInit client = new ClientInit(ownerAddress);
            client.start();
            Message mes = new Message(Message.TEXT_MESSAGE, "Banjo", null, "Client");
            mes.setUser_record("Client");
            new SendMessageClient(this, ownerAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mes);
            Log.e("Test", "Message hydrated, start SendMessageClient AsyncTask");
        }
    }
}
