package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 *
 * Maintain the data around max scores per level per game.
 */
public class GameScoreCard {
    private int gameScore = 0;
    private int gameLevel = 0;

    public GameScoreCard(int gameLevelId, int gameCurrentScore) {
        this.gameLevel = gameLevelId;
        this.gameScore = gameCurrentScore;
    }

    public int getGameScore() { return this.gameScore; }
    public int getGameLevel() { return this.gameLevel; }

    public String toString() {
        StringBuilder respString = new StringBuilder("GameScoreCard: (");
        respString.append("Level: " + gameLevel + ", ");
        respString.append("Score: " + gameScore + ")");

        return respString.toString();
    }
}
