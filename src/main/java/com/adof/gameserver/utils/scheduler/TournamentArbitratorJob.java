package com.adof.gameserver.utils.scheduler;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.dao.tmgr.Tournament;
import com.adof.gameserver.manager.TournamentController;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mukthar on 25/11/16.
 */
public class TournamentArbitratorJob implements Job {
    private String LogTag = "# [TournamentArbitratorJob]: ";
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized void execute(JobExecutionContext context) throws JobExecutionException {
        Calendar cal = Calendar.getInstance();
        System.out.println("\n===========================================");
        System.out.println("# HelloJob: \"" + sdf.format(cal.getTime()) + "\" Tournament Arbitrator Started.");
        System.out.println("===========================================\n");

        TournamentArbitrator();
    }


    public void TestTournamentArbitrator(List<Tournament> allTournaments) {
        System.out.println(LogTag + " Arbitrator job has started...");

        int maxScore = 0;
        User winner = new User();
        User previousWinner = new User();

        for (Tournament tournament : allTournaments) {

            if (tournament.getActiveUsersCount() > 0) {
                List<User> tournamentUsers = tournament.getActiveUsers();

                for (User currentUser : tournamentUsers) {

                    if (currentUser.getGameScore() > previousWinner.getGameScore()) {
                        previousWinner = winner;
                        previousWinner.setWinnerFlag(false);

                        winner = currentUser;
                        currentUser.setWinnerFlag(true);
                        maxScore = currentUser.getGameScore();
                    }
                }

                tournament.setWinner(winner);
                System.out.println("Winner: " + winner.toString() + ", with MAX-Score = " + maxScore);

                /** Coin updated thread to be started here */
                TournamentController.getInstance()
                        .getCoinManagerInstance()
                        .updateWinnerWonCoins(winner, tournament.getTournamentCoinPrize());


            }
            else {
                System.out.println("TournamentArbitrator: No users to process");

            }

        }

        /** Ingest into the tournament_history */
        ingestTournamentHistory(allTournaments);
        System.out.println("Tournament History: " + allTournaments);

    }

    public void TournamentArbitrator() {
        System.out.println(LogTag + " Arbitrator job has started...");

        List<Tournament> allTournaments = new ArrayList<>();
        allTournaments.addAll(
                TournamentController.getInstance()
                        .getManagerInstance()
                        .getActiveTournaments()
        );

        allTournaments.addAll(
                TournamentController
                        .getInstance()
                        .getManagerInstance()
                        .getActiveTournaments()
        );


        int maxScore = 0;
        User winner = new User();
        User previousWinner = new User();

        for (Tournament tournament : allTournaments) {

            if (tournament.getActiveUsersCount() > 0) {
                List<User> tournamentUsers = tournament.getActiveUsers();

                for (User currentUser : tournamentUsers) {

                    if (currentUser.getGameScore() > previousWinner.getGameScore()) {
                        previousWinner = winner;
                        previousWinner.setWinnerFlag(false);

                        winner = currentUser;
                        currentUser.setWinnerFlag(true);
                        maxScore = currentUser.getGameScore();
                    }
                }

                tournament.setWinner(winner);
                System.out.println("Winner: " + winner.toString() + ", with MAX-Score = " + maxScore);

                /** Coin updated thread to be started here */
                TournamentController.getInstance()
                        .getCoinManagerInstance()
                        .updateWinnerWonCoins(winner, tournament.getTournamentCoinPrize());


            }
            else {
                System.out.println("TournamentArbitrator: No users to process");

            }

        }

        /** Ingest into the tournament_history */
        ingestTournamentHistory(allTournaments);
        System.out.println("Tournament History: " + allTournaments);
    }

    private void ingestTournamentHistory(List<Tournament> tournamentList) {
        System.out.println(LogTag + "Inserting records into Db.");
        String insertSql = "INSERT INTO tournament_history"
                + " (user_id, app_id, tid, score, max_req_users, min_req_users, active_user_count, " +
                "is_winner, refund_coins, created_at, updated_at, coins_won, tournament_prize) " +
                "VALUES"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        runInsertQuery(tournamentList, insertSql);
    }



    private void runInsertQuery(List<Tournament> tournamentList, String insertSql) {
        /**
         (1) Refund_coins (2) won_coins & total_coins - update process
         is all handled below
         */
        boolean shouldRefund = false;

        for (Tournament tournament : tournamentList) {
            List<User> tournamentUsers = tournament.getActiveUsers();

            if (tournament.getActiveUsersCount() < tournament.getMinRequiredUsers()) {
                shouldRefund = true;
                batchUpdate(shouldRefund, tournament, tournamentUsers, insertSql);

                for (User userToBeRefunded : tournamentUsers) {
                    /** Coin updated thread to be started here */
                    TournamentController.getInstance()
                            .getCoinManagerInstance()
                            .refundFeeCoins(userToBeRefunded, tournament.getTournamentCoinPrize());
                }

            } else {
                batchUpdate(shouldRefund, tournament, tournamentUsers, insertSql);

            }
        }

    }


    /*
     id                | integer                     | not null default nextval('tournament_history_id_seq'::regclass)
     user_id           | integer                     |
     app_id            | integer                     |
     tid               | integer                     |
     score             | integer                     |
     max_req_users     | integer                     |
     min_req_users     | integer                     |
     active_user_count | integer                     |
     is_winner         | boolean                     |
     refund_coins      | boolean                     |
     created_at        | timestamp without time zone | not null
     updated_at        | timestamp without time zone | not null
     coins_won         | integer                     |
     tournament_prize  | integer
    */

    public void batchUpdate(boolean shouldRefund, Tournament tournament, List<User> tournamentUsers,
                            String insertSql) {

        Connection dbConnection = PostgresConnectionPool.getInstance().getConnectionFromPool();
        /** 0 by default as we start assuming that the user is not winner */
        int coins_won = 0;

        Calendar cal = Calendar.getInstance();

        try {

            PreparedStatement preparedStatement = null;
            for (User activeUser : tournamentUsers) {
                if (dbConnection == null) {
                    System.out.println(LogTag + "DB Connection is NULL.");
                }

                preparedStatement = dbConnection.prepareStatement(insertSql);

                int userUuid = activeUser.getUuid();

                /** coins_won logic */
                if ( activeUser.getIsWinner() ) {
                    coins_won = tournament.getTournamentCoinPrize();
                }

                /*
                 (user_id, app_id, tid, score, max_req_users, min_req_users, active_user_count, " +
                "is_winner, refund_coins, created_at, updated_at, coins_won, tournament_prize) " +
                 */
                preparedStatement.setInt(1, userUuid);
                preparedStatement.setInt(2, 2); // Hard coded for Connect The Clock
                preparedStatement.setInt(3, tournament.getTid());
                preparedStatement.setInt(4, activeUser.getGameScore());
                preparedStatement.setInt(5, tournament.getMaxRequiredUsers());
                preparedStatement.setInt(6, tournament.getMinRequiredUsers());
                preparedStatement.setInt(7, tournament.getActiveUsersCount());

                boolean isAwinner = activeUser.getIsWinner();
                preparedStatement.setBoolean(8, isAwinner);
                preparedStatement.setBoolean(9, shouldRefund);
                preparedStatement.setTimestamp(10, Timestamp.valueOf(sdf.format(cal.getTime())));
                preparedStatement.setTimestamp(11, Timestamp.valueOf(sdf.format(cal.getTime())));

                preparedStatement.setInt(12, coins_won);
                preparedStatement.setInt(13, tournament.getTournamentCoinPrize());
                preparedStatement.addBatch();
            }


            preparedStatement.executeBatch();
            dbConnection.commit();

        } catch (SQLException e) {

            e.printStackTrace();
//            try {
//                dbConnection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }

        } finally {

//            if (preparedStatement != null) {
//                try {
//                    preparedStatement.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }

            if (dbConnection != null) {
                PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
            }

        }

        System.out.println("Record is inserted into DBUSER table!");
    }

    public static void checkUpdateCounts(int[] updateCounts) {
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                System.out.println("OK; updateCount=" + updateCounts[i]);
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                System.out.println("OK; updateCount=Statement.SUCCESS_NO_INFO");
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                System.out.println("Failure; updateCount=Statement.EXECUTE_FAILED");
            }
        }
    }

    public static void main(String[] args) {
        //new TournamentArbitratorJob().Arbitrator();


        User user1 = new User(1666, "muks", "muks@gmail.com", "muksfb", 33, "male");

        List<Tournament> tournamentsList = new ArrayList<>();
        Tournament t1 = new Tournament("Daily 20", 1, 20, 120, 2, 2, true, 99);
        Tournament t2 = new Tournament("Daily 50", 2, 50, 120, 2, 2, true, 99);

        System.out.println(user1.getUuid());
        t1.addUser(user1);
        tournamentsList.add(t1);
        //tournamentsList.add(t2);

        new TournamentArbitratorJob().TestTournamentArbitrator(tournamentsList);
    }

}