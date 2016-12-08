package com.adof.gameserver.helpers;

import com.adof.gameserver.dao.payloads.request.ClientPayload;
import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.dao.payloads.response.EndTournament;
import com.adof.gameserver.dao.payloads.response.PlayPayload;
import com.adof.gameserver.manager.EnvironmentManager;
import com.adof.gameserver.manager.TournamentConcluder;
import com.adof.gameserver.utils.datetime.DateTimeExtra;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;
import com.adof.gameserver.factory.ScoreFraudAuditor;
import com.adof.gameserver.dao.tmgr.Tournament;
import com.adof.gameserver.dao.payloads.response.ResponsePayload;
import com.adof.gameserver.dao.payloads.response.StartPayload;
import com.adof.gameserver.manager.TournamentController;
import com.adof.gameserver.manager.TournamentManager;
import com.adof.gameserver.factory.UsersFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.adof.gameserver.utils.datetime.DateTimeExtra.getCurrentTimeStamp;

/**
 * Created by mukthar on 19/11/16.
 * <p>
 * 1. Play tournament
 * 2. Start tournament
 * 3. End tournament
 */

public class GamePlayHelpers {
    private String LogTag = "# [" + this.getClass().getSimpleName() + "]: ";


    /**
     * Play-Tournament request coming from screen # 1
     * "payload": {
     * "category": "play_tournament",
     * },
     */
    public synchronized ResponsePayload playTournament(ClientPayload clientPayload) {
        int currentUuid = clientPayload.getUser().getUuid();

        System.out.println("\n" + LogTag + "\"Tournament Play\" request from user.");

        /** Check if user exists in the users table */
        //String currentUuid = uniqUserId.substring(1, uniqUserId.length() - 1);

        UsersFactory usersFactory = new UsersFactory();
        HashMap<Integer, Boolean> sysUsers = usersFactory.getUsersInSystem();

        //System.out.println(sysUsers.toString());

        if (!sysUsers.containsKey(currentUuid)) {
            System.out.println(LogTag + "NEW user.");
            ingestNewTournamentUser(currentUuid);

        } else {
            System.out.println(LogTag + "NOT a New user.");
        }


        /** Get the list of tournaments
         * Also takes care of bug: where user cannot enter the tournament which he has already participated */
        List<Tournament> tournamentsList = getTournamentsListForUser(currentUuid);

        /** Fetch if the daily_bonus_claim */
        int dailyBonusFlag = 0;
        int bonus_coins = 0;
        int total_coins = 0;


        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        //select total_coins from users where user_id = 'uuid'
        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);
        String gameScoreSelectSql = "select total_coins, daily_bonus_claimed from users where id = \'" + currentUuid + "\'";
        ResultSet resultSet = PostgresDbHelper.executeSelectSql(dbConnection, gameScoreSelectSql);

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("\n===");
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);

                    if (columnName.equalsIgnoreCase("total_coins")) {
                        total_coins = resultSet.getInt(columnName);
                    } else if (columnName.equalsIgnoreCase("daily_bonus_claimed")) {
                        dailyBonusFlag = resultSet.getInt(columnName);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (dbConnection != null) {
                PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
            }

        }

        /**
         * Get requested tournament ID and its feeCoins
         *
         */
        if (total_coins >= 20) {

        }


        /** If user was not thrown with bonus on a daily threshold, then bonus_coins = 50 */
        if (dailyBonusFlag == 0) {
            /** Get the daily_bonus coins after querying Db */
            bonus_coins = EnvironmentManager.getInstance().getDailyBonusCoins();
            total_coins = total_coins + bonus_coins;
        }

        /** User has to claim bonus_coins for total_coins to sum up with bonus_coins */
        System.out.println(LogTag + "Daily_Bonus_Flag = " + dailyBonusFlag
                + ", Total_Coins=" + total_coins + ", DailyBonusCoins = " + bonus_coins);

        return new PlayPayload(bonus_coins, total_coins, tournamentsList);
    }


    /**
     * Start tournament - algorithm
     * "payload": {
     * "category": "start_tournament",
     * "tid": 1, (Tournament ID)
     * "time_stamp": "2016-11-19 15:01:01"
     * },
     */

    public synchronized ResponsePayload startTournament(ClientPayload clientPayload) {
        System.out.println("\n" + LogTag + "\"Tournament Start\" request from user.");


        /** 1. Recalculate his total coins by reducing that by "used_coins" or utilised_coins (CoinManager)
         2. Can play tournament based on, if its less than 50th minute
         */
        String respMsg = "";
        int entryRestrictMinute = EnvironmentManager.getInstance().getEntryRestrictMinute();
        boolean canPlayTournament;
        if (new DateTimeExtra().getTimeParams("minute") <= entryRestrictMinute) {
            canPlayTournament = true;
            respMsg = "Entered the tournament successfully.";
        } else {
            canPlayTournament = false;
            respMsg = "Cannot enter the tournament, its full/completed. " +
                    "Please chose another tournament to play.";
        }


        /** enter the tournament by its tid */
        TournamentController tController = TournamentController.getInstance();
        TournamentManager MANAGER = tController.getManagerInstance();
        MANAGER.participate(clientPayload.getPayload().getTournamentId(),
                clientPayload.getUser(),
                clientPayload.getPayload().getCoins());

        /** Keep polling for data availability */
        boolean newUserAcceptedFlag = MANAGER.participationAccepted();

        if (!newUserAcceptedFlag) {
            for (int i = 0; i < 10; i++) {
                System.out.println(LogTag + " Pooling if the \"start-tournament\" is done... ");

                newUserAcceptedFlag = MANAGER.participationAccepted();
                if (!newUserAcceptedFlag) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            respMsg = "# Tournament is full/already started. Please chose another tournament.";
        } else {
            System.out.println("# All Good...");
            respMsg = "Entered the tournament successfully.";
        }


        HashMap<String, String> processedData = MANAGER.getProcessData();
        System.out.println("+# Process Data: " + processedData.toString());
        MANAGER.participationReset();


        /** Fetch the final no. of total_coins from coins-manager thread */
        int finalTotalCoins =
                TournamentController.getInstance()
                        .getCoinManagerInstance()
                        .getRecalculatedTotalCoins();

        System.out.println(LogTag + " Final value for total_coins: " + finalTotalCoins);

        return new StartPayload(clientPayload.getPayload().getTournamentId(),
                new User(),
                canPlayTournament,
                finalTotalCoins,
                respMsg,
                processedData.get("revised_tid"));
    }


    /**
     * End tournament - algorithm
     * "payload": {
     * "category": "end_tournament",
     * "tid": 1, (Tournament ID)
     * "score": {
     * "level_id":  1,
     * "score":  500000 (A hacked sore)
     * }
     * },
     */
    public synchronized ResponsePayload endTournament(ClientPayload clientPayload) {
        System.out.println("\n" + LogTag + "\"Tournament End\" request from user.");

        /** Compare the scores submitted from the score card */
        int gameId = 1; /** ToDo: Hard coded for Connect the clocks */
        String endTourRespMsg = "Score submitted successfully.";

        boolean isScoreSubmitted = false;

        boolean isFraudScore = new ScoreFraudAuditor().isFraudScore(
                gameId,
                clientPayload.getPayload().getGameScoreCard().getGameLevel(),
                clientPayload.getPayload().getGameScoreCard().getGameScore()
        );


        if (isFraudScore) {
            System.out.println(LogTag + " - submitted score is FRAUD.");
            endTourRespMsg = "Fraudulent score.";

        } else {
            System.out.println(LogTag + " - submitted score is NOT fraud.");

            /** enter the tournament by its tid */
            int score = clientPayload.getPayload().getGameScoreCard().getGameScore();
            int gameLevel = clientPayload.getPayload().getGameScoreCard().getGameLevel();

            /** Ingest the scores and levels to the user object */
            clientPayload.getUser().setGameScore(score);
            clientPayload.getUser().setGameLevel(gameLevel);

            System.out.println(LogTag + "#Tournament End Details=(Score: " + score + ", Level: " + gameLevel + ")");

            TournamentController tController = TournamentController.getInstance();
            TournamentConcluder tConclluder = tController.getTournamentConcluderInstance();


            tConclluder.endUserTournament(
                    clientPayload.getPayload().getTournamentId(),
                    clientPayload.getUser(),
                    clientPayload.getPayload().getCoins()
            );

            isScoreSubmitted = true;

        }
        return new EndTournament(isFraudScore, isScoreSubmitted, endTourRespMsg);
    }


    private List<Tournament> getTournamentsListForUser(int currentUuid) {
        //String currentUuid = userId.substring(1, userId.length() - 1);

        List<Tournament> toReturnTournamentsList = new ArrayList<>();
        List<Tournament> tournamentsList =
                TournamentController
                        .getInstance()
                        .getManagerInstance()
                        .getActiveTournaments();


        /**
         Identify repeated tournament requests - from user based on
         tid and feeCoins
         */
        for (Tournament activeTournament : tournamentsList) {
            List<User> activeUsers = activeTournament.getActiveUsers();

            boolean userNotInList = true;
            for (User activeUser : activeUsers) {
                int chompedUuid = activeUser.getUuid();
                //String chompedUuid = rawUuid.substring(1, rawUuid.length() - 1);

                if (chompedUuid == currentUuid) {
                    userNotInList = false;
                    break;
                }
            }

            if (userNotInList) {
                toReturnTournamentsList.add(activeTournament);
            }
        }

        return toReturnTournamentsList;

    }


    public void ingestNewTournamentUser(int uuid) {
        Connection dbConnection = PostgresConnectionPool.getInstance().getConnectionFromPool();
        PreparedStatement preparedStatement = null;

        String insertQuery = " INSERT INTO users (created_at, updated_at, id, bonus_coins, daily_bonus_claimed) " +
                "values (?, ?, ?, ?, ?)";

        try {
            preparedStatement = dbConnection.prepareStatement(insertQuery);

            preparedStatement.setTimestamp(1, getCurrentTimeStamp());
            preparedStatement.setTimestamp(2, getCurrentTimeStamp());
            preparedStatement.setInt(3, uuid);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);

            // execute insert SQL stetement
            preparedStatement.executeUpdate();
            dbConnection.commit();
            preparedStatement.close();

            System.out.println("Record is inserted into DBUSER table!");

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (dbConnection != null) {
                    PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
                }

            } catch(SQLException e){
                e.printStackTrace();
            }

        }

    }

}
