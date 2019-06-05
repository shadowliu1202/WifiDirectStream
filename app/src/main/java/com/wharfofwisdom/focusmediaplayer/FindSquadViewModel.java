package com.wharfofwisdom.focusmediaplayer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.CreateSquad;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.squad.SearchAndJoinSquad;
import com.wharfofwisdom.focusmediaplayer.domain.model.hardware.Kiosk;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;

import io.reactivex.Single;

public class FindSquadViewModel extends ViewModel {
    private final MutableLiveData<String> status = new MutableLiveData<>();
    private final Kiosk kiosk;
    private final SquadRepository repository;

    FindSquadViewModel(Kiosk soldier, SquadRepository p2PRepository) {
        this.kiosk = soldier;
        status.postValue("系統初始化...");
        repository = p2PRepository;
    }

    public LiveData<String> status() {
        return status;
    }

    Single<Squad> initializeSquad() {
        return decideSquad().doOnSuccess(squad -> status.postValue("隊伍" + squad.name() + "創建成功"));
    }

    private Single<Squad> decideSquad() {
        if (hasSquad(kiosk)) {
            status.postValue("加入隊伍中...");
            return new SearchAndJoinSquad(kiosk.squad(), repository).execute().doOnSuccess(squad -> status.postValue("成功加入隊伍:" + squad.name()));
        } else {
            if (kiosk.hasInternet()) {
                status.postValue("創建隊伍(" + kiosk.name() + ")中...");
                return new CreateSquad(kiosk, repository).execute().doOnSuccess(squad -> status.postValue("創建隊伍:" + squad.name()));
            } else {
                status.postValue("尋找隊伍中...");
                return new SearchAndJoinSquad(repository).execute().doOnSuccess(squad -> status.postValue("成功加入隊伍:" + squad.name()));
            }
        }
    }

    private boolean hasSquad(Kiosk kiosk) {
        return !kiosk.squad().equals(Squad.NO_SQUAD);
    }
}
