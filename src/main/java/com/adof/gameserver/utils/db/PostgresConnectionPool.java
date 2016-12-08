package com.adof.gameserver.utils.db;


import com.adof.gameserver.manager.EnvironmentManager;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by mukthar on 20/11/16.
 *
 * DB connection pool object
 */
public class PostgresConnectionPool {
    private static String LogTag = "# [PostgresConnectionPool]: ";

    private static PostgresConnectionPool instance = null;

    private static int MAX_POOL_SIZE = 50;      /** default no. of connections */
    private static List<Connection> dbConnections = new ArrayList<>();

    private static File PROPS_FILE = new File("/opt/tomcat9/webapps/tas/WEB-INF/classes/com/adof/games/tas.properties");

    private static String DB_DRIVER = null;
    private static String DB_CONNECTION = null;
    private static String DB_USER = null;
    private static String DB_PASSWORD = null;

    static {
        System.out.println(":= Static initializer for Db connection pool.");
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(PROPS_FILE);
            // load a properties file
            prop.load(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            System.out.println("Key : " + key + ", Value : " + value);
        }


        DB_DRIVER = prop.getProperty("db.driver");
        DB_CONNECTION = "jdbc:postgresql://" + prop.getProperty("db.host")
                + ":" + prop.getProperty("db.port") + "/" + prop.getProperty("db.name");
        DB_USER = prop.getProperty("db.user");
        DB_PASSWORD = prop.getProperty("db.password");

        /** Initialize db connections */

        MAX_POOL_SIZE = EnvironmentManager.getInstance().getMaxDbConnections();
        initializeConnectionPool();
    }


    public void setPropsFilePath(File propsFilePath) {
        this.PROPS_FILE = propsFilePath;
    }

    private PostgresConnectionPool() {
        // Exists only to defeat instantiation.
    }

    public static PostgresConnectionPool getInstance() {
        if (instance == null) {
            instance = new PostgresConnectionPool();
        }
        return instance;
    }

//    public PostgresConnectionPool() {
//        initializeConnectionPool();
//    }

    private static void initializeConnectionPool() {
        while( !isConnectionPoolFilled() ) {
            dbConnections.add(createNewConnectionForPool());
        }
        System.out.println(":= Initialization complete...");
    }

    public void setMaxPoolSize(int maxPoolSize) {
        MAX_POOL_SIZE = maxPoolSize;
    }

    private static synchronized boolean isConnectionPoolFilled() {
        int activeConnections = dbConnections.size();
        if (activeConnections < MAX_POOL_SIZE) {
            return false;
        } else {
            return true;
        }
    }

    //Creating a connection
    private static Connection createNewConnectionForPool() {
        Connection dbConnection;
        try {
            Class.forName(DB_DRIVER);

            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,DB_PASSWORD);
            dbConnection.setAutoCommit(false);

            return dbConnection;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Opened database successfully");

        return null;
    }

    public synchronized Connection getConnectionFromPool() {
        Connection connection = null;
        if (dbConnections.size() > 0) {
            connection = dbConnections.get(0);
            dbConnections.remove(0);
        }
        else {
            System.out.println(LogTag + " DB Connection pool is empty.");
            return null;
        }

        System.out.println(LogTag + "DbConnection pool size (Claim) = " + dbConnections.size());

        return connection;
    }

    public synchronized void submitConnectionBackToPool(Connection usedDbConn) {
        if (usedDbConn != null) {
            dbConnections.add(usedDbConn);
        } else {
            System.out.println(LogTag + "Cannot submit a null connection");
        }
        System.out.println(LogTag + "DbConnection pool size (Submit) = " + dbConnections.size());
    }



    public synchronized void releaseConnections() {
        System.out.println(":= Size of DB Connection pool - " + dbConnections.size());
        while (dbConnections.size() != 0) {
            Connection conn = dbConnections.get(0);
            dbConnections.remove(0);
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println(":= Conn pool is empty now Size = " + dbConnections.size());
    }
}
