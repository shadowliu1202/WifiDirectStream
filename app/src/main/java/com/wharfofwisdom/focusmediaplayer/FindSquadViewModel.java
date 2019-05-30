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

import io.reactivex.Single;

public class FindSquadViewModel extends ViewModel {
    private final MutableLiveData<String> status = new MutableLiveData<>();
    private final Soldier soldier;
    private final SquadRepository repository;

    FindSquadViewModel(Soldier soldier) {
        this.soldier = soldier;
        status.postValue("系統初始化...");
        repository = null;
    }

    public LiveData<String> status() {
        return status;
    }

    Single<Squad> decideSquad() {
        if (soldier.belongToSquad()) {
            return new SearchSquad(soldier.getSquadName(), repository).execute()
                    .flatMap(squad -> new JoinSquad(squad, soldier, repository).execute());
        }
        if (soldier.isLeader()) {
            return new CreateSquad(soldier, repository).execute();
        }
        return new SearchSquad(repository).execute();
    }
}
