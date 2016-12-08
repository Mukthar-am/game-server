package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 22/11/16.
 */
public class Score {
    private int gameLevel = 0;
    private int gameLevelScore = 0;

    public Score(int level, int score) {
        this.gameLevel = level;
        this.gameLevelScore = score;
    }

    public int getGameLevelScore() { return this.gameLevelScore; }
    public int getGameLevel() { return this.gameLevel; }

    public String toString() {
        StringBuilder respObj = new StringBuilder("Score=(");
        respObj.append("Level: " + this.gameLevel + ", ");
        respObj.append("Score: " + this.gameLevelScore + ", ");
        respObj.append(")");

        return respObj.toString();
    }
}
