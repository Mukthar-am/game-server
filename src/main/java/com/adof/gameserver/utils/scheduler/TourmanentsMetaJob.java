package com.adof.gameserver.utils.scheduler;

import com.adof.gameserver.dao.tmgr.Tournament;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mukthar on 18/11/16.
 * <p>
 * Entire tournament structure
 */

public class TourmanentsMetaJob {
    private static String LogTag = "[TourmanentsMetaJob]: ";
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static TourmanentsMetaJob instance = null;
    private static List<Tournament> tournamentsList = new ArrayList<>();

    static AtomicInteger tournamentIncId = new AtomicInteger();

    private TourmanentsMetaJob() {}

    static {
        System.out.println(LogTag + "Static init called.");
        getTournaments();
    }

//    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        /*
//         * ToDo: This should be populated from Db table every hour
//         */
//        getTournaments();
//    }

    public List<Tournament> getTournamentList() {
        getTournaments();
        return tournamentsList;
    }

    public static TourmanentsMetaJob getInstance() {
        if (instance == null) {
            instance = new TourmanentsMetaJob();
        }
        return instance;
    }


    private static void getTournaments() {
        System.out.println(LogTag + "#:= Refresh on tournaments metadata...");
        Calendar cal = Calendar.getInstance();
        System.out.println(sdf.format(cal.getTime()));

        tournamentsList = new ArrayList<>();

        /**
         * id, coins, session_timeout, created_at, updated_at, min_req_users, max_req_users, tournament_prize
         */
        String selectSql = "SELECT * FROM tournament_meta";
        Connection dbConnection = PostgresConnectionPool.getInstance().getConnectionFromPool();
        ResultSet resultsSet = PostgresDbHelper.executeSelectSql(dbConnection, selectSql);

        try {
            ResultSetMetaData rsmd = resultsSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            int id, coins, sessionTimeout, minRequiredUsers, maxReqUsers, tournamentPrize;
            id = coins = sessionTimeout = minRequiredUsers = maxReqUsers = tournamentPrize = 0;

            while (resultsSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);

                    if (columnName.equalsIgnoreCase("created_at") ||
                            columnName.equalsIgnoreCase("updated_at")) {
                        //System.out.println("Skipping " + columnName);
                        continue;
                    }

                    int rowValue = resultsSet.getInt(columnName);

                    switch (columnName) {
                        case "id":
                            id = rowValue;
                            break;
                        case "coins":
                            coins = rowValue;
                            break;

                        case "session_timeout":
                            sessionTimeout = rowValue;
                            break;

                        case "min_req_users":
                            minRequiredUsers = rowValue;
                            break;

                        case "max_req_users":
                            maxReqUsers = rowValue;
                            break;


                        case "tournament_prize":
                            tournamentPrize = rowValue;
                            break;

                        default:
                            break;
                    }

                }

                /** keep adding tournamets meta, reading from Db */
                tournamentsList.add(
                        new Tournament("Tournament " + id,
                                id,
                                coins,
                                sessionTimeout,
                                minRequiredUsers,
                                maxReqUsers,
                                false,
                                tournamentPrize)
                );

            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        finally {
            System.out.println(LogTag + "Release DbConnection...");

            if (dbConnection != null) {
                PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
            }
        }
    }


    public static void main(String[] args) {
        /** Init Tournaments metedata */
        TourmanentsMetaJob tmjInstance = TourmanentsMetaJob.getInstance();
        System.out.println(tmjInstance.getTournamentList().toString());

    }

}
