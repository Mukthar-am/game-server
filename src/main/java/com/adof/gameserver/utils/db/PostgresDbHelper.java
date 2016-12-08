package com.adof.gameserver.utils.db;

import java.sql.*;

/**
 * Created by mukthar on 18/11/16.
 * <p>
 * PostgresDbHelper
 */
public abstract class PostgresDbHelper {
    private static String LogTag = PostgresDbHelper.class.getSimpleName() + ": ";

    public static ResultSet executeSelectSql(Connection dbConnection, String selectSql) {
        System.out.println(LogTag + "Executing the SQL: " + selectSql);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            preparedStatement = dbConnection.prepareStatement(selectSql);
            resultSet = preparedStatement.executeQuery();

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        } finally {
            System.out.println(LogTag + "#:= Finally is not implemented yet.");
        }


        return resultSet;
    }


    public static void resultsSetIterator(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            System.out.println("\n===");
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {

                    String columnName = metaData.getColumnName(i);
                    String columnTypeName = metaData.getColumnTypeName(i);

                    if (columnName.equalsIgnoreCase("created_on") ||
                            columnName.equalsIgnoreCase("updated_on")) {
                        System.out.println("Skipping " + columnName);
                        continue;
                    }

//                    String rowCellValue = null;
//                    if (columnTypeName.equalsIgnoreCase("bigserial")
//                            || columnTypeName.equalsIgnoreCase("int4")) {
//
//                    }
//                    else if (columnTypeName.equalsIgnoreCase("")) {
//
//                    }

                    String rowCellValue = String.valueOf(resultSet.getInt(columnName));
                    System.out.println(LogTag + columnName + " = " + rowCellValue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static int getTotalCoinsForUser(Connection dbConnection, int uuid) {

        int totalCoinsToReturn = 0;
        String getTotalCoinsSql = "select total_coins from users where id = " + uuid + "";
        ResultSet resultSet = PostgresDbHelper.executeSelectSql(dbConnection, getTotalCoinsSql);

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (resultSet.next()) {
                String columnName = metaData.getColumnName(1);
                totalCoinsToReturn = resultSet.getInt(columnName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }

        return totalCoinsToReturn;
    }


//    public void ingestNewTournamentUser(Connection dbConnection) throws SQLException {
//        PreparedStatement preparedStatement = null;
//
//        String insertSql = "INSERT INTO DBUSER"
//                + "(USER_ID, USERNAME, CREATED_BY, CREATED_DATE) VALUES"
//                + "(?,?,?,?)";
//
//        try {
//            preparedStatement = dbConnection.prepareStatement(insertSql);
//
//            preparedStatement.setInt(1, 11);
//            preparedStatement.setString(2, "mkyong");
//            preparedStatement.setString(3, "system");
//            preparedStatement.setTimestamp(4, getCurrentTimeStamp());
//
//            // execute insert SQL stetement
//            preparedStatement.executeUpdate();
//
//            System.out.println("Record is inserted into DBUSER table!");
//
//        } catch (SQLException e) {
//
//            System.out.println(e.getMessage());
//
//        } finally {
//
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
//
//            if (dbConnection != null) {
//                dbConnection.close();
//            }
//
//        }
//
//    }
//
//    private static java.sql.Timestamp getCurrentTimeStamp() {
//        java.util.Date today = new java.util.Date();
//        return new java.sql.Timestamp(today.getTime());
//    }
//
//


    public static void main(String[] args) {
//        int total_coins = 10;
//        int usedCoins = 1;
//        String uniqUserId = "uuid";
//
//        PostgresConnectionPool connectionPoolInstance = PostgresConnectionPool.getInstance();
//        Connection dbConnection = connectionPoolInstance.getConnectionFromPool();
//
//        String updateSql = "UPDATE users SET total_coins = ?"
//                + ", used_coins = ? where user_id = ?";
//
//        try
//        {
//            // create our java preparedstatement using a sql update query
//            PreparedStatement ps = dbConnection.prepareStatement(updateSql);
//
//            // set the preparedstatement parameters
//            ps.setInt(1, total_coins);
//            ps.setInt(2, usedCoins);
//            ps.setString(3, uniqUserId);
//
//
//            // call executeUpdate to execute our sql update statement
//            ps.executeUpdate();
//            dbConnection.commit();
//            ps.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }

}
