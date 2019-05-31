package com.wharfofwisdom.focusmediaplayer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.SquadRepository;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.command.CreateSquad;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.command.JoinSquad;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.command.SearchSquad;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.Soldier;
import com.wharfofwisdom.focusmediaplayer.domain.model.squad.position.Squad;
import com.wharfofwisdom.focusmediaplayer.domain.repository.p2p.P2PRepository;

import io.reactivex.Single;

public class FindSquadViewModel extends ViewModel {
    private final MutableLiveData<String> status = new MutableLiveData<>();
    private final Soldier soldier;
    private final SquadRepository repository;

    FindSquadViewModel(Soldier soldier, P2PRepository p2PRepository) {
        this.soldier = soldier;
        status.postValue("系統初始化...");
        repository = p2PRepository;
    }

    public LiveData<String> status() {
        return status;
    }

    Single<Squad> decideSquad() {
        if (soldier.belongToSquad()) {
            status.postValue("加入隊伍中...");
            return new SearchSquad(soldier.getSquadName(), repository).execute()
                    .flatMap(squad -> new JoinSquad(squad, soldier, repository).execute());
        }
        if (soldier.isLeader()) {
            status.postValue("創建隊伍(" + soldier.getSquadName() + ")中...");
            return new CreateSquad(soldier, repository).execute();
        }
        status.postValue("尋找隊伍中...");
        return new SearchSquad(repository).execute();
    }
}
