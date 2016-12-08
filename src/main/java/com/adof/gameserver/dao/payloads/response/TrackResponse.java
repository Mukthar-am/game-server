package com.adof.gameserver.dao.payloads.response;

/**
 * Created by mukthar on 22/11/16.
 */
public class TrackResponse {
    private int totalCoins = 0;
    private int purchasedCoins = 0;
    private int coinsWon = 0;
    private int earnedCoins = 0;
    private int utilisedCoins = 0;
    private int bonusCoins = 0;
    private int redeemdedCoins = 0;

    public TrackResponse() {}

    public void settotal_coins(int coins) { this.totalCoins = coins;}
    public void setpurchased_coins(int coins) { this.purchasedCoins = coins;}
    public void setcoins_Won(int coins) { this.coinsWon = coins;}
    public void setearned_coins(int coins) { this.earnedCoins = coins;}
    public void setutilised_coins(int coins) { this.utilisedCoins = coins;}
    public void setredeemded_oins(int coins) { this.redeemdedCoins = coins;}
    public void setbonus_coins(int coins) { this.bonusCoins = coins;}


    public int gettotal_coins(){ return this.totalCoins; }
    public int getpurchased_coins(){ return this.purchasedCoins; }
    public int getcoins_won(){ return this.coinsWon; }
    public int getearned_coins(){ return this.earnedCoins; }
    public int getutilised_coins(){ return this.utilisedCoins; }
    public int getbonus_coins(){ return this.bonusCoins; }
    public int getredeemed_coins() { return this.redeemdedCoins; }

}
