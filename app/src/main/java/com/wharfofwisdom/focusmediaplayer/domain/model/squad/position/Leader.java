package com.wharfofwisdom.focusmediaplayer.domain.model.squad.position;

import java.util.ArrayList;
import java.util.List;

public abstract class Leader {

    List<Member> members = new ArrayList<>();

    public abstract void createSquad();

    public void addMember(Member member) {
        members.add(member);
    }

}
