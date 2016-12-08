package com.adof.gameserver.factory;

import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.db.PostgresDbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by mukthar on 29/11/16.
 */
public class UsersFactory {
    private String LogTag = "# [UsersFactory]: ";
    private HashMap<Integer, Boolean> usersInSystem = new HashMap<>();

    public HashMap<Integer, Boolean> getUsersInSystem() {
        if (usersInSystem.size() == 0) {
            String selectSql = "select id from users";
            Connection dbConnection = PostgresConnectionPool.getInstance().getConnectionFromPool();
            ResultSet resultsSet = PostgresDbHelper.executeSelectSql(dbConnection, selectSql);

            try {
                ResultSetMetaData metaData = resultsSet.getMetaData();

                System.out.println("\n" + LogTag);
                while (resultsSet.next()) {
                    String columnName = metaData.getColumnName(1);
                    int rowCellValue = resultsSet.getInt(columnName);
                    usersInSystem.put(rowCellValue, true);
                }

            } catch (SQLException e) {
                e.printStackTrace();

            } finally {
                try {
                    if (resultsSet != null) {
                        resultsSet.close();
                    }

                    if (dbConnection != null) {
                        PostgresConnectionPool.getInstance().submitConnectionBackToPool(dbConnection);
                    }
//
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            return usersInSystem;
        }

        return usersInSystem;
    }


    public static void main(String[] args) {
        UsersFactory usersFactory = new UsersFactory();
        HashMap<Integer, Boolean> sysUsers = usersFactory.getUsersInSystem();

        System.out.println(sysUsers.toString());
    }
}
