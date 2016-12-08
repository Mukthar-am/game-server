package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 * Game benchmarkings read from Db
 *
 */

public class GameScoreBenchmarks {
    private int gameId = 0;
    private String gameName = null;
    private LevelScoreMap levelScoreMap = null;

    public GameScoreBenchmarks(int gid, String gname, LevelScoreMap levelScoreMap) {
        this.gameId = gid;
        this.gameName = gname;
        this.levelScoreMap = levelScoreMap;
    }




    /** level to score mapping */
    class LevelScoreMap {
        int levelId = 0;
        int maxScoreByLevel = 0;

        public LevelScoreMap(int levelid, int levelscore) {
            this.levelId = levelid;
            this.maxScoreByLevel = levelscore;
        }
    }
}
