package com.adof.gameserver.dao.payloads.response;

/**
 * Created by mukthar on 19/11/16.
 */
public interface ResponsePayload {
    int total_coins = 0;
    boolean can_play = false;
    String message = null;

    public int gettotal_coins();
    public boolean getcan_play();
    public String getmessage();
}
