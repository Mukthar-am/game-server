package com.adof.gameserver.dao.payloads.response;

import com.adof.gameserver.dao.payloads.request.User;

/**
 * Created by mukthar on 19/11/16.
 *
 * Response object when "start tournament" is triggered
 *
 * Incoming request :
     "payload": {
         "category": "start_tournament",
         "tid": 1, (Tournament ID)
         "time_stamp": yyyy-MM-dd HH:mm:ss
     },
 */
public class StartPayload implements ResponsePayload {
    private int tid = 0;
    private int totalCoinsRecalculated = 0;
    private User user = null;
    private boolean can_play = false;
    private String message = null;
    private String revised_tid = null;


    public StartPayload(int tid, User user, boolean canPlay, int totalCoins, String respMsg, String rtid) {
        this.tid = tid;
        this.user = user;
        this.totalCoinsRecalculated = totalCoins;
        this.can_play = canPlay;
        this.message = respMsg;
        this.revised_tid = rtid;
    }

    @Override
    public int gettotal_coins() {
        return totalCoinsRecalculated;
    }
    public boolean getcan_play() { return this.can_play; }
    public String getmessage() { return this.message; }
    public String getRevised_tid() { return this.revised_tid; }

}
