package com.adof.gameserver.utils.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mukthar on 21/11/16.
 */
public class HelloJob implements Job {
    private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public synchronized void execute(JobExecutionContext context) throws JobExecutionException {
        Calendar cal = Calendar.getInstance();
        System.out.println("\n===========================================");
        System.out.println("# HelloJob: \"" + sdf.format(cal.getTime()) + "\" Hello muks, Quartz job scheduled and running ");
        System.out.println("===========================================\n");
    }
}
