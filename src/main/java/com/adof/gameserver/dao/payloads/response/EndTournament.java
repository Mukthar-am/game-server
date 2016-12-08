package com.adof.gameserver.dao.payloads.response;

/**
 * Created by mukthar on 19/11/16.
 *
     "payload": {
         "category": "end_tournament",
         "tid": 1, (Tournament ID)
         "score": {
             "level_id":  1,
                "score":  500000 (A hacked sore)
         }
     },

 */
public class EndTournament implements ResponsePayload {
    private boolean isFraudScore = false;
    private boolean isScoreSubmitted = false;
    private String message = null;

    public EndTournament(boolean isFraud, boolean scoreSubmitted, String msg) {
        this.message = msg;
        this.isFraudScore = isFraud;
        this.isScoreSubmitted = scoreSubmitted;
    }


    public boolean getis_fraud_score() {
        return isFraudScore;
    }
    public boolean getis_score_submitted() {
        return isScoreSubmitted;
    }


    @Override
    public int gettotal_coins() {
        return 0;
    }

    @Override
    public boolean getcan_play() {
        return false;
    }

    @Override
    public String getmessage() {
        return this.message;
    }

}
