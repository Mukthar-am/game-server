package com.adof.gameserver.factory;


import com.adof.gameserver.dao.tour.GameScoreMeta;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by mukthar on 23/11/16.
 * <p>
 * - Score fraud check utilities
 * <p>
 * TODO: Code optimisation in the form of factory pattern for GamesScoreMeta()
 */
public class ScoreFraudAuditor {
    private String LogTag = this.getClass().getSimpleName() + ": ";
    public GameScoreMeta gameScoreMeta = new GameScoreMeta();

    public ScoreFraudAuditor() {
        getGameScoreMeta();
    }

    private void getGameScoreMeta() {
        if (this.gameScoreMeta.getSize() == 0 || this.gameScoreMeta == null) {
            initScoreCards();
        }
    }

    public synchronized boolean isFraudScore(int gameId, int gameLevel, int gameLevelScore) {
        System.out.println(LogTag + gameLevelScore + " <= " + this.gameScoreMeta.getScoreMap(gameId).get(gameLevel));
        if (gameScoreMeta == null) {
            System.out.println(LogTag + "GameScoreMeta is NULL.");
        }

        if (gameLevelScore <= this.gameScoreMeta.getScoreMap(gameId).get(gameLevel)) {
            return false;
        } else {
            return true;
        }

    }


    private HashMap<Integer, Integer> initScoreCards() {
        /** TODO: Hard coded for "connect the clocks", should be later taken for other games as well */
        int gameId = 1;
        HashMap<Integer, Integer> gameLevelScoreMap = new HashMap<>();


        Connection dbConnection = PostgresConnectionPool.getInstance().getConnectionFromPool();

        String gameScoreSelectSql = "select game_level, level_max_score from game_scores where app_id = " + gameId;
        ResultSet resultSet = PostgresDbHelper.executeSelectSql(dbConnection, gameScoreSelectSql);

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                int level = 0;
                int maxScore = 0;

                for (int i = 1; i <= columnCount; i++) {

                    String columnName = metaData.getColumnName(i);

                    if (columnName.equalsIgnoreCase("created_at") ||
                            columnName.equalsIgnoreCase("updated_at")) {
                        System.out.println("Skipping " + columnName);
                        continue;
                    }


                    if (columnName.equalsIgnoreCase("game_level")) {
                        level = resultSet.getInt(columnName);
                    } else if (columnName.equalsIgnoreCase("level_max_score")) {
                        maxScore = resultSet.getInt(columnName);
                    }
                }

                gameLevelScoreMap.put(level, maxScore);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (dbConnection != null) {
                PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        this.gameScoreMeta = new GameScoreMeta(gameId, gameLevelScoreMap);
        System.out.println(this.gameScoreMeta.toString());

        return gameLevelScoreMap;
    }


    public static void main(String[] args) {
        //System.out.println( ScoreFraudAuditor.getInstance().isFraudScore(1, 1, 5) );
//        ScoreFraudAuditor scoreFraudAuditor = new ScoreFraudAuditor();
//        GameScoreMeta gameScoreMeta= scoreFraudAuditor.getGameScoreMeta();
//        System.out.println("=== " + scoreFraudAuditor.isFraudScore(gameScoreMeta, 1, 1, 5) );

        System.out.println(new ScoreFraudAuditor().isFraudScore(1, 2, 12));
    }
}
