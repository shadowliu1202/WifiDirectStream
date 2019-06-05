package com.wharfofwisdom.focusmediaplayer.domain.model.squad.position;

import java.util.ArrayList;
import java.util.List;

public abstract class Leader {

    public abstract Squad squad();

    List<Follower> followers = new ArrayList<>();

    public void addFollowers(Follower follower) {
        followers.add(follower);
    }

    public void removeFollowers(Follower follower) {
        followers.remove(follower);
    }

}
