package com.adof.gameserver.utils.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JobScheduler {
    private static JobScheduler instance = null;
    private Scheduler scheduler = null;

    private JobScheduler() {}

    public static JobScheduler getInstance() {
        if (instance == null) {
            instance = new JobScheduler();
        }

        return instance;
    }

    public void startScheduler() {
        JobDetail helloJob = JobBuilder.newJob(HelloJob.class)
                .withIdentity("helloJob", "Group1").build();

        Trigger helloJobTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("helloJobTrigger", "Group1")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 20 0/1 * * ?").withMisfireHandlingInstructionFireAndProceed())
                .build();


        JobDetail dailyBonusResetJob = JobBuilder.newJob(DailyBonusResetJob.class)
                .withIdentity("dailyBonusResetJob", "Group2").build();

        Trigger bonusResetJobTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("bonusResetJobTrigger", "Group2")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 0 * * ?").withMisfireHandlingInstructionFireAndProceed())
                .build();


        JobDetail arbitratorJob = JobBuilder.newJob(TournamentArbitratorJob.class)
                .withIdentity("arbitratorJob", "Group3").build();

        Trigger arbitratorJobTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("arbitratorJobTrigger", "Group3")
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 56 0/1 * * ?").withMisfireHandlingInstructionFireAndProceed())
                .build();

        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
            this.scheduler.start();

            this.scheduler.scheduleJob(helloJob, helloJobTrigger);
            this.scheduler.scheduleJob(dailyBonusResetJob, bonusResetJobTrigger);
            this.scheduler.scheduleJob(arbitratorJob, arbitratorJobTrigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JobScheduler.getInstance().startScheduler();
    }

    public void shutdownScheduler() {
        try {
            this.scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
