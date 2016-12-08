package com.adof.gameserver.tas;

import com.adof.gameserver.manager.CoinManager;
import com.adof.gameserver.manager.EnvironmentManager;
import com.adof.gameserver.manager.TournamentController;
import com.adof.gameserver.utils.db.PostgresConnectionPool;
import com.adof.gameserver.utils.scheduler.JobScheduler;
import com.adof.gameserver.utils.scheduler.TourmanentsMetaJob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * Created by mukthar on 20/11/16.
 */

@WebListener
public class TasInitializer implements ServletContextListener {
    private String LogTag = "[TasInitializer]: ";
    private String APPLICATION_CLASS_DIR = "webapps/tas/WEB-INF/classes";
    private String RESOURCE_DIR = "com/adof/games";
    private String PROPS_FILE = "tas.properties";
    private CoinManager coinManagerInstance = null;
    private Thread thread = null;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println(LogTag + "====== Starting up!");
        String catalinaBaseDir = System.getProperty("catalina.base");

        /** TODO: remove tas web app path is hard coded here */
        String propsFilePath = catalinaBaseDir
                + "/" + APPLICATION_CLASS_DIR
                + "/" + RESOURCE_DIR
                + "/" + PROPS_FILE;

        File propsFile = new File(propsFilePath);
        if (propsFile.exists()) {
            String propsFileAbsPath = propsFile.getAbsolutePath();
            System.out.println("Properties file: " + propsFileAbsPath);
        } else {
            System.out.println("# Warning: Could not find the " + PROPS_FILE + " file.");
        }

        /** Just initialize env vars from properties file */
        EnvironmentManager.getInstance();

        /** Init Tournaments metedata */
        TourmanentsMetaJob tmjInstance = TourmanentsMetaJob.getInstance();
        System.out.println(tmjInstance.getTournamentList().toString());


        this.coinManagerInstance = TournamentController.getInstance().getCoinManagerInstance();
        if (coinManagerInstance != null) {
            thread = new Thread(this.coinManagerInstance);
            thread.start();
        } else {
            System.out.println("# Coin manager instance is NULL.");
        }

        /** All jobs scheduler */
        JobScheduler.getInstance().startScheduler();

        /** Postgres db connection initializer */
        PostgresConnectionPool.getInstance();


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        /** Release Db connection objects */
        PostgresConnectionPool.getInstance().releaseConnections();

        /** Stop/kill coin manager thread */
        this.coinManagerInstance.stopCoinManager();

        /** Shutdown job scheduler */
        JobScheduler.getInstance().shutdownScheduler();

        System.out.println("Shutting down!");
    }
}
