package com.adof.gameserver.manager;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.dao.tmgr.Tournament;

import java.util.concurrent.Callable;

/**
 * Created by mukthar on 20/11/16.
 */
public class TournamentEntityThread implements Callable {
    private ThreadLocal<String> name =  new ThreadLocal<>();
    private Tournament tournament = null;

    TournamentEntityThread(Tournament thisTournament) {
        this.tournament = thisTournament;
    }

    public void addUsersToTournament(User newUser) {
        this.tournament.addUser(newUser);
    }

    @Override
    public Integer call() throws Exception {
        return 1;
    }
}
