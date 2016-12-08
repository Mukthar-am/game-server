package com.adof.gameserver.dao.tour;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by mukthar on 23/11/16.
 */
public class GameScoreMeta {
    private HashMap<Integer, HashMap<Integer, Integer>> scoreMap = new HashMap<>();

    public GameScoreMeta() {}

    public GameScoreMeta(int gid, HashMap<Integer, Integer> scoreMap) {
        this.scoreMap.put(gid, scoreMap);
    }

    public HashMap<Integer, Integer> getScoreMap(int gameId) {
        return this.scoreMap.get(gameId);
    }

    public int getSize() { return this.scoreMap.size(); }

    public String toString() {
        Set<Integer> gameIds = this.scoreMap.keySet();

        StringBuilder sb = new StringBuilder("[GameId=");
        for (Integer gameId : gameIds) {
            sb.append(gameId + "(" + scoreMap.get(gameId).toString() + ") ");
        }
        sb.append("]");

        return scoreMap.toString();
    }

}
