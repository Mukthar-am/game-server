package com.adof.gameserver.dao.payloads.request;

import java.util.Date;

/**
 * Created by mukthar on 18/11/16.
 *
 * This object is just the inner payload part of the clientpayload request coming in.
 *
 */



public class ActionPayload {
    /** total_coins, purchased_coins, coins_won, redeemed_coins, earned_coins, utilised_coins,
     * claim_bonus_coins,
     * start_tournament, end_tournament, play_tournament
     * */
    private String payload_category = null;

    /** Possible values = video_ad , app_download, banner) */
    private String coinsSrc = null;
    private int coins = 0;
    private Date timeStamp = null; /** yyyy-MM-dd HH:mm:ss */
    private int tournamentId = 0;
    private double spent = 0;
    private GameScoreCard gameScoreCardCard = null;


    public GameScoreCard getGameScoreCard() { return this.gameScoreCardCard; }
    public String getCategory() { return this.payload_category; }
    public String getCoinsSrc() { return this.coinsSrc; }
    public int getCoins() { return this.coins; }
    public double getSpent() { return this.spent; }
    public Date getTimeStamp() { return this.timeStamp; }
    public int getTournamentId() { return this.tournamentId; }
    //public int getGameLevelCurrentScore() { return this.getGameLevelCurrentScore(); }


    public ActionPayload(String category,
                         String coinsSrc,
                         int coins,
                         double spent,
                         int tournamentId,
                         int gameLevelId,
                         int gameCurrentScore,
                         Date timeStamp) {

        this.payload_category = category;
        this.coinsSrc = coinsSrc;
        this.coins = coins;
        this.spent = spent;
        this.timeStamp = timeStamp;
        this.tournamentId = tournamentId;

        this.gameScoreCardCard = new GameScoreCard(gameLevelId, gameCurrentScore);
    }

    public String toString() {
        StringBuilder objDetails = new StringBuilder("ActionPayload=(");
        objDetails.append("Category=" + this.payload_category + ",");
        objDetails.append("CoinSrc=" + this.coinsSrc + ",");
        objDetails.append("Coins=" + this.coins + ",");
        objDetails.append("Spent=" + this.spent + ",");
        objDetails.append("TournamentId=" + this.tournamentId + ",");
        objDetails.append(this.gameScoreCardCard.toString() + ",");
        objDetails.append("TimeStamp=" + this.timeStamp+ ")");

        return objDetails.toString();
    }


}
