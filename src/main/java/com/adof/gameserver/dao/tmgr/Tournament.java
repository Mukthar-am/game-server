package com.adof.gameserver.dao.tmgr;

import com.adof.gameserver.dao.payloads.request.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mukthar on 19/11/16.
 * Tournament object
 */
public class Tournament {
    private String name = null;
    private int tid = 0;
    private int t_fee_coins = 0;
    private int t_session = 0;
    private String t_session_unit = "seconds";
    private boolean is_active_flag = false;
    private List<User> activeUsers = new ArrayList<>();
    private int minRequiredUsers = 0;
    private int maxRequiredUsers = 0;
    private int tournamentCoinPrize = 0;
    private User winner = new User();


    public Tournament(String name, int tid, int fee, int t_sess, int minReqUsers, int maxReqUsers, boolean is_active_flag, int tCoinPrize) {
        this.name = name;
        this.tid = tid;
        this.t_fee_coins = fee;
        this.t_session = t_sess;
        this.minRequiredUsers = minReqUsers;
        this.maxRequiredUsers = maxReqUsers;
        this.is_active_flag = is_active_flag;
        this.tournamentCoinPrize = tCoinPrize;
    }

    /** setter */
    public void setTid(int newTid) {
        this.tid = newTid;
    }

    public void addUser(User newUser) {
        this.activeUsers.add(newUser);
    }

    public void addUser(int uuid, String name, String emailId, String fbId, int age, String gender) {
        this.activeUsers.add(new User(uuid, name, emailId, fbId, age, gender));
    }

    public String getName() { return this.name; }
    public int getTid() {
        return this.tid;
    }
    public int getFeeCoins() {
        return t_fee_coins;
    }
    public int getSession() {
        return t_session;
    }
    public String getSessionUnit() { return t_session_unit; }

    public boolean getIsTournamentActive() {
        return this.is_active_flag;
    }

    public void markAsClosed() { this.is_active_flag = false; }

    public int getMaxRequiredUsers() { return this.maxRequiredUsers; }
    public int getMinRequiredUsers() { return this.minRequiredUsers; }

    public int getActiveUsersCount() {
        return this.activeUsers.size();
    }
    public List<User> getActiveUsers() { return this.activeUsers; }
    public int getTournamentCoinPrize() { return this.tournamentCoinPrize; }

    public void setWinner(User tWinner) { this.winner = tWinner; }
    public User getWinner() { return this.winner; }


    public String toString() {
        StringBuilder respStr = new StringBuilder("[Tid: " + this.tid + "=(");
        respStr.append("Name: " + this.name + ", ");
        respStr.append("Tid: " + this.tid + ", ");
        respStr.append("Coins: " + this.t_fee_coins + ", ");
        respStr.append("ActiveUserCount: " + this.activeUsers.size() + ", ");
        respStr.append("MaxUsers: " + this.maxRequiredUsers + ", ");
        respStr.append("MinUsers: " + this.minRequiredUsers + ", ");

        respStr.append("Session: " + this.t_session + ", ");
        respStr.append("Session Unit: " + this.t_session_unit + ", ");
        respStr.append("Is Tournament Active: " + this.is_active_flag + ", ");
        respStr.append("Tournament Prize: " + this.tournamentCoinPrize + ", ");
        //respStr.append("Active Users: " + this.activeUsers.toString() + ", ");

        respStr.append("Winner: " + winner.toString() );

        respStr.append(")]");

        return respStr.toString();
    }

}
