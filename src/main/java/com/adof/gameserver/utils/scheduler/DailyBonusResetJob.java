package com.adof.gameserver.utils.scheduler;

import com.adof.gameserver.utils.db.PostgresConnectionPool;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mukthar on 25/11/16.
 *  Cron job to reset daily_bonus_claimed for all users
 */

public class DailyBonusResetJob implements Job {
    private String LogTag = "# [DailyBonusResetJob]: ";

    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("# job scheduler started.... ");

        Calendar cal = Calendar.getInstance();
        System.out.println("\n===========================================");
        System.out.println(LogTag + sdf.format(cal.getTime()));

        //resetDailyBonusClaimedFlag();

        System.out.println("===========================================\n");
    }

    private synchronized void resetDailyBonusClaimedFlag() {
        System.out.println(LogTag + " Daily_Bonus_Claimed_Flag update process for all users...");

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        //update users set daily_bonus_claimed = 0;
        String updateSql = "UPDATE users SET daily_bonus_claimed = ?";

        try {
            PreparedStatement ps = dbConnection.prepareStatement(updateSql);
            ps.setInt(1, 0);

            // call executeUpdate to execute our sql update statement
            ps.executeUpdate();
            dbConnection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
    }

}
