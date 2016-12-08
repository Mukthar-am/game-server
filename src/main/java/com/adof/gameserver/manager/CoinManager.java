package com.adof.gameserver.manager;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by mukthar on 24/11/16.
 */
public class CoinManager implements Runnable {
    private String LogTag = "# " + this.getClass().getSimpleName() + ": ";

    private volatile boolean terminate = false;
    private volatile boolean coinManagerHealthCheck = false;
    private volatile boolean markUsedCoins = false;
    private volatile boolean updateWonCoins = false;
    private volatile boolean startCoinRefunding = false;

    private volatile int TOTAL_COINS_RECALCULATED = 0;
    private volatile int TOTAL_COINS_BONUS_CLAIMED = 0;

    private User user = new User();
    private User userAsWinner = new User();
    private User refundUser = new User();

    private int usedCoins = 0;
    private int coinsWon = 0;
    private int coinsToRefund = 0;

    @Override
    public void run() {
        while (!terminate) {
            /** Check the status of the tournament */
            if (coinManagerHealthCheck) {
                processHealthCheck();
            }


            if (markUsedCoins) {
                System.out.println(LogTag + "Coin Manager started....");
                processUsedCoins();
            }

            if (updateWonCoins) {
                updateWonCoinsForWinner();
            }


            if (startCoinRefunding) {
                updateWonCoinsForWinner();
            }
        }


        System.out.println(LogTag + ":= CoinManager thread terminated.");
    }


    public void stopCoinManager() {
        this.terminate = true;
    }

    public void healthCheck() {
        this.coinManagerHealthCheck = true;
    }

//    private User claimUser = null;
//    public void claimDailyBonus(User claimingUser) {
//        this.claimUser = claimingUser;
//        this.dailyBonusClaimedFlag = true;
//    }

    private void processHealthCheck() {
        System.out.println("\n" + LogTag + "System is healthy today.");
        coinManagerHealthCheck = false;
    }

    public int getRecalculatedTotalCoins() { return this.TOTAL_COINS_RECALCULATED; }



    public void markUsedCoins(User user, int usedCoins) {
        this.user = user;
        this.usedCoins = usedCoins;

        markUsedCoins = true;
    }


    public void updateWinnerWonCoins(User user, int wonCoins) {
        this.userAsWinner = user;
        this.coinsWon = wonCoins;

        updateWonCoins = true;
    }



    public void refundFeeCoins(User user, int refundCoins) {
        this.refundUser = user;
        this.coinsToRefund = refundCoins;

        startCoinRefunding = true;
    }


     /**
     * get total_coins for the user from Db
     * get used_coins by tid
     *
     * re-calculate total_coins - used_coins
     * and update both total_coins and used_coins
     */
    private synchronized void processUsedCoins() {
        System.out.println(LogTag + " Coin update process...");
        int uniqUserId = this.user.getUuid();

        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);
        int USED_COINS = this.usedCoins;

        System.out.println(LogTag + "UsedCoins: " + USED_COINS + ", UUID: " + uniqUserId);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        //select total_coins from users where user_id = 'uuid'
        int totalCoinsCurrent = PostgresDbHelper.getTotalCoinsForUser(dbConnection, uniqUserId);

        this.TOTAL_COINS_RECALCULATED = (totalCoinsCurrent - USED_COINS);

        System.out.println(LogTag + "Pre total_coins: " + totalCoinsCurrent);
        System.out.println(LogTag + "Update/Post total_coins: " + this.TOTAL_COINS_RECALCULATED);
        String updateSql = "UPDATE users SET total_coins = ?, used_coins = ? where id = ?";

        PreparedStatement ps = null;
        try {
            ps = dbConnection.prepareStatement(updateSql);

            ps.setInt(1, this.TOTAL_COINS_RECALCULATED);
            ps.setInt(2, USED_COINS);
            ps.setInt(3, uniqUserId);


            // call executeUpdate to execute our sql update statement
            ps.executeUpdate();
            dbConnection.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);

        markUsedCoins = false;    /** Stop the coin update thread else it will repeat the updates */
    }


    /**
     * get total_coins for the user from Db
     * get used_coins by tid
     *
     * re-calculate total_coins - used_coins
     * and update both total_coins and used_coins
     */
    private synchronized void addBonusCoinsToTotalCoins() {
        System.out.println(LogTag + " Updating total-coins witih bonus-coins...");
        int uniqUserId = this.user.getUuid();

        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);
        int USED_COINS = this.coinsWon;

        //System.out.println(LogTag + "UsedCoins: " + USED_COINS + ", UUID: " + UUID);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        //select total_coins from users where user_id = 'uuid'
        int totalCoinsCurrent = PostgresDbHelper.getTotalCoinsForUser(dbConnection, uniqUserId);

        this.TOTAL_COINS_RECALCULATED = (totalCoinsCurrent + USED_COINS);

        System.out.println(LogTag + "Pre total_coins: " + totalCoinsCurrent);
        System.out.println(LogTag + "Update/Post total_coins: " + this.TOTAL_COINS_RECALCULATED);
        String updateSql = "UPDATE users SET total_coins = ?, used_coins = ? where id = ?";

        try {
            PreparedStatement ps = dbConnection.prepareStatement(updateSql);

            ps.setInt(1, this.TOTAL_COINS_RECALCULATED);
            ps.setInt(2, USED_COINS);
            ps.setInt(3, uniqUserId);

            // call executeUpdate to execute our sql update statement
            ps.executeUpdate();
            dbConnection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);

        markUsedCoins = false;    /** Stop the coin update thread else it will repeat the updates */
    }


    /** This part takes care of fetching daily_bonus_flag = 0 | 1 from users table */
    private synchronized void updateWonCoinsForWinner() {
        System.out.println(LogTag + " Claim Daily Bonus flag update process...");

        int uniqUserId = this.userAsWinner.getUuid();
        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);

        System.out.println(LogTag + "Updating with User with coins-won = " + this.coinsWon + ", for uniqUserId: " + uniqUserId);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        /** get current total_coins */
        int totalCoinsCurrent = PostgresDbHelper.getTotalCoinsForUser(dbConnection, uniqUserId);
        int totalCoinsRecalculatedWithWonCoins = (totalCoinsCurrent + this.coinsWon);

        String updateSql = "UPDATE users SET total_coins = ?, coins_won = ? where id = ?";

        try {
            PreparedStatement ps = dbConnection.prepareStatement(updateSql);
            ps.setInt(1, totalCoinsRecalculatedWithWonCoins);
            ps.setInt(2, this.coinsWon);
            ps.setInt(3, uniqUserId);

            ps.executeUpdate();
            dbConnection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);

        updateWonCoins = false;    /** Stop the coin update thread else it will repeat the updates */
    }


    /** This part takes care of fetching daily_bonus_flag = 0 | 1 from users table */
    private synchronized void coinRefunder() {
        System.out.println(LogTag + " coinRefunder() update process...");

        int uniqUserId = this.refundUser.getUuid();
        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);

        System.out.println(LogTag + "Updating with User with coins-won = " + this.refundUser + ", for uniqUserId: " + uniqUserId);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        /** get current total_coins */
        int totalCoinsCurrent = PostgresDbHelper.getTotalCoinsForUser(dbConnection, uniqUserId);
        int totalCoinsRecalculatedWithRefund = (totalCoinsCurrent - this.coinsToRefund);

        String updateSql = "UPDATE users SET total_coins = ? where id = ?";

        try {
            PreparedStatement ps = dbConnection.prepareStatement(updateSql);
            ps.setInt(1, totalCoinsRecalculatedWithRefund);
            ps.setInt(2, uniqUserId);

            ps.executeUpdate();
            dbConnection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);

        startCoinRefunding = false;    /** Stop the coin update thread else it will repeat the updates */
    }

}
