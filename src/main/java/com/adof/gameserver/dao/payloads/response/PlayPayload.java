package com.adof.gameserver.dao.payloads.response;

import com.adof.gameserver.dao.tmgr.Tournament;

import java.util.List;

/**
 * Created by mukthar on 19/11/16.
 */
public class PlayPayload implements ResponsePayload {
    private int bonus_coins = 0;
    private int total_coins = 0;
    private boolean can_play = false;
    private String message = null;
    private List<Tournament> tournamentsList = null;

    public PlayPayload(int bonus_coins, int total_coins,
                           List<Tournament> tList) {
        this.bonus_coins = bonus_coins;
        this.total_coins = total_coins;
//        this.can_play = canPlay;
        this.message = message;
        this.tournamentsList = tList;
    }

    public int getbonus_coins() { return this.bonus_coins; }
    public int gettotal_coins() { return this.total_coins; }
    public boolean getcan_play() { return this.can_play; }
    public String getmessage() { return this.message; }
    public List<Tournament> gettournaments() { return this.tournamentsList; }
}
