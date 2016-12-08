package com.adof.gameserver.manager;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by mukthar on 25/11/16.
 */
public class ClaimBonusThread implements Runnable {
    private String LogTag = "# " + this.getClass().getSimpleName() + ": ";

    private volatile boolean terminate = false;
    private volatile boolean claimDailyBonusFlag = false;

    private volatile boolean isTotalCoinsRecalculated = false;
    private volatile int TOTAL_COINS_RECALCULATED = 0;

    private User claimUser = null;

    public ClaimBonusThread(User user) {
        this.claimUser = user;
    }

    @Override
    public void run() {
        while (!terminate) {
            if (claimDailyBonusFlag) {
                claimDailyBonusFlag();
            }
        }
        System.out.println(LogTag + ":= ClaimBonus thread terminated.");
    }


    public void stopClaimBonusThread() {
        this.terminate = true;
    }

    public void claimDailyBonus() {
        this.claimDailyBonusFlag = true;
    }
    public int getRecalculatedTotalCoins() {
        return this.TOTAL_COINS_RECALCULATED;
    }


    public boolean isClaimProcCompleted() {
        return this.isTotalCoinsRecalculated;
    }


    /** This part takes care of fetching daily_bonus_flag = 0 | 1 from users table */
    private synchronized void claimDailyBonusFlag() {
        System.out.println(LogTag + " Daily Claim Daily Bonus flag update process...");

        int bonusCoinsClaimedFlag = 1;  /** Claimed */
        int uniqUserId = this.claimUser.getUuid();
        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);

        System.out.println(LogTag + "Updating with : " + bonusCoinsClaimedFlag + ", for uniqUserId: " + uniqUserId);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        /** get current total_coins */
        int totalCoinsCurrent = PostgresDbHelper.getTotalCoinsForUser(dbConnection, uniqUserId);
        int dailyBonusCoins = EnvironmentManager.getInstance().getDailyBonusCoins();

        this.TOTAL_COINS_RECALCULATED = (totalCoinsCurrent + dailyBonusCoins);

        String updateSql = "UPDATE users SET total_coins = ?, bonus_coins = ?, daily_bonus_claimed = ? where id = ?";
        System.out.println(LogTag + " Setting up recalculated total_coins = " + TOTAL_COINS_RECALCULATED);
        try {
            PreparedStatement ps = dbConnection.prepareStatement(updateSql);
            ps.setInt(1, this.TOTAL_COINS_RECALCULATED);
            ps.setInt(2, dailyBonusCoins);
            ps.setInt(3, bonusCoinsClaimedFlag); /** Marking bonus_coins = 0 which means, bonus is used for the day */
            ps.setInt(4, uniqUserId);

            ps.executeUpdate();
            dbConnection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);

        System.out.println(LogTag + "Setting TOTAL_COINS_BONUS_CLAIMED = " + TOTAL_COINS_RECALCULATED);
        isTotalCoinsRecalculated = true;
        claimDailyBonusFlag = false;    /** Stop the coin update thread else it will repeat the updates */
    }


}
