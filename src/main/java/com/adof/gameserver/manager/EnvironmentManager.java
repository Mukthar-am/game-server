package com.adof.gameserver.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by mukthar on 24/11/16.
 */
public class EnvironmentManager {
    private static String LogTag = "[EnvironmentManager]: ";
    private static EnvironmentManager instance = null;

    private File PROPS_FILE = new File("/opt/tomcat9/webapps/tas/WEB-INF/classes/com/adof/games/tas.properties");

    private String dbPassword = null;
    private String dbUser = null;
    private String dbPort = null;
    private String dbName = null;
    private String dbHost = null;
    private String dbDriver = null;
    private int dailyBonusCoins = 50;
    private int maxDbConnections = 50;
    private int tournamentEntryMinuteRestrict = 55;

    {
        initialize();
    }

    private EnvironmentManager() {}

    public static EnvironmentManager getInstance() {
        if(instance == null) {
            instance = new EnvironmentManager();
        }
        return instance;
    }

    public void setConfigFile(String configFile) {
        PROPS_FILE = new File(configFile);
    }


    private void initialize() {
        System.out.println(LogTag + "= Instance initializer for environment variables.");
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

            switch (key) {
                case "db.driver":
                    this.dbDriver = value;
                    break;

                case "db.host":
                    this.dbHost = value;
                    break;

                case "db.port":
                    this.dbPort = value;
                    break;

                case "db.name":
                    this.dbName = value;
                    break;

                case "db.password":
                    this.dbPassword = value;
                    break;

                case "db.user":
                    this.dbUser = value;
                    break;

                case "daily.bonus.coins":
                    this.dailyBonusCoins = Integer.parseInt(value);

                    break;

                case "tounament.entry.restrict.minute":
                    this.tournamentEntryMinuteRestrict = Integer.parseInt(value);
                    break;

                case "db.max.connections":
                    this.maxDbConnections = Integer.parseInt(value);
                    break;

                default:
                    System.out.println("# Could not understand the input - " + key);
                    break;
            }
        }
    }

    public int getEntryRestrictMinute() { return this.tournamentEntryMinuteRestrict; }
    public int getDailyBonusCoins() { return this.dailyBonusCoins; }

    public String getDbUser() { return this.dbUser; }
    public String getDbPassword() { return this.dbPassword; }
    public String getDbHost() { return  this.dbHost; }
    public String getDbPort() { return this.dbPort; }
    public String getDbDriver() { return this.dbDriver; }
    public String getDbName() { return this.dbName; }

    public int getMaxDbConnections() { return this.maxDbConnections; }

    public String toString() {
        StringBuilder sb = new StringBuilder("[Env Vars: ");
        sb.append("DbUser: " + this.dbUser + ", ");
        sb.append("DbPassword: " + this.dbPassword+ ", ");
        sb.append("DbHost: " + this.dbHost+ ", ");
        sb.append("DbPort: " + this.dbPort + ", ");
        sb.append("DbName: " + this.dbName+ ", ");
        sb.append("DbDriver: " + this.dbDriver+ "]");

        return sb.toString();
    }


    public static void main(String[] args) {
        EnvironmentManager instane = EnvironmentManager.getInstance();
        System.out.println(instane.toString());

    }
}
