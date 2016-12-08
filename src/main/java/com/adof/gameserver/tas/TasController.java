package com.adof.gameserver.tas;

import com.adof.gameserver.dao.payloads.request.ClientPayload;

import com.adof.gameserver.dao.payloads.response.TrackResponse;
import com.adof.gameserver.manager.ClaimBonusThread;
import com.adof.gameserver.manager.TournamentController;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * touralytics web API
 * URI: http://localhost:8080/tas/track
 */

@RestController
@RequestMapping("track")
public class TasController {
    private String LogTag = "[TasController]: ";

    //private ClientPayload clientPayload = null;

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
    @ResponseBody
    public TrackResponse tas(@RequestBody String requestBodyJson) {
        TrackResponse response = new TrackResponse(); /** Takes care of resetting total_coins */

        ClientPayload clientPayload = new ClientPayload().parseAndGet(requestBodyJson);
        System.out.println(LogTag + "========================");
        System.out.println(LogTag + clientPayload.toString());
        System.out.println(LogTag + "========================");


        /** total_coins, purchased_coins, coins_won, redeemed_coins, earned_coins, utilised_coins,
         * claim_bonus_coins,
         * start_tournament, end_tournament, play_tournament
         * */
        String category = clientPayload.getPayload().getCategory();
        String action_category = category.substring(1, category.length() - 1);
        System.out.println("# " + action_category);


        switch (action_category.toLowerCase()) {
            case "purchased_coins":
                System.out.println("# " + action_category);
                break;

            case "coins_won":
                System.out.println("# " + action_category);
                break;

            case "redeemed_coins":
                System.out.println("# " + action_category);
                break;

            case "earned_coins":
                System.out.println("# " + action_category);
                break;

            case "utilised_coins":
                System.out.println("# " + action_category);
                break;

            case "claim_bonus":
                System.out.println(LogTag + "Perform - " + action_category);

                ClaimBonusThread claimBonusThread = new ClaimBonusThread(clientPayload.getUser());
                Thread thread = new Thread(claimBonusThread);
                thread.start();

                claimBonusThread.claimDailyBonus();

                boolean isClaimDone = claimBonusThread.isClaimProcCompleted();
                if (!isClaimDone) {
                    System.out.println(LogTag + " Will start polling - " + isClaimDone);
                    for (int i = 0; i < 10; i++) {

                        isClaimDone = claimBonusThread.isClaimProcCompleted();

                        if ( !isClaimDone ) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            break;
                        }
                    }
                }

                response.settotal_coins(claimBonusThread.getRecalculatedTotalCoins());
                claimBonusThread.stopClaimBonusThread();
                claimBonusThread = null;

                System.out.println(LogTag + "Claim bonus recalculated and set: " + response.gettotal_coins());

                break;
            case "total_coins":
                System.out.println("# " + action_category);
                break;

            default:
                System.out.println("# WARNING: Invalid action category.");
                break;
        }

        return (response);
    }


    private int total_coins = 0;
    private int bonus_coins = 0;
    private int purchased_coins = 0;
    private int coins_won = 0;
    private int earned_coins = 0;
    private int used_coins = 0;
    private int redeemed_coins = 0;

    private void getCoins(ClientPayload clientPayload) {
        int uniqUserId = clientPayload.getUser().getUuid();
        //String UUID = uniqUserId.substring(1, uniqUserId.length() - 1);

        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();

        String gameScoreSelectSql
                = "SELECT " +
                "total_coins, " +
                "bonus_coins, " +
                "purchased_coins, " +
                "coins_won, " +
                "earned_coins, " +
                "used_coins, " +
                "redeemed_coins " +
                "FROM users WHERE id = \'" + uniqUserId + "\'";

        ResultSet resultSet = PostgresDbHelper.executeSelectSql(dbConnection, gameScoreSelectSql);

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                String columnName = metaData.getColumnName(1);
                int noOfCoins = resultSet.getInt(columnName);

                switch (columnName) {
                    case "total_coins":
                        this.total_coins = noOfCoins;
                        break;

                    case "bonus_coins":
                        this.bonus_coins = noOfCoins;
                        break;

                    case "purchased_coins":
                        this.purchased_coins = noOfCoins;
                        break;

                    case "coins_won":
                        this.coins_won = noOfCoins;
                        break;

                    case "earned_coins":
                        this.earned_coins = noOfCoins;
                        break;

                    case "used_coins":
                        this.used_coins = noOfCoins;
                        break;

                    case "redeemed_coins":
                        this.redeemed_coins = noOfCoins;
                        break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
                connectionPoolInstance.submitConnectionBackToPool(dbConnection);
            }

        }
    }
}