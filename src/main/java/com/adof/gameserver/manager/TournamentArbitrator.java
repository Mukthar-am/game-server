package com.adof.gameserver.manager;

import com.adof.gameserver.utils.datetime.DateTimeExtra;

/**
 * Created by mukthar on 23/11/16.
 *
 * Thread declaring winner of all the tournaments
 *
 */
public class TournamentArbitrator implements Runnable{
    private String LogTag = "# " + this.getClass().getSimpleName() + ": ";
    private volatile boolean terminate = false;

    @Override
    public void run() {
        while (!terminate) {


            if (new DateTimeExtra().getTimeParams("minute") == 00) {

            }

        }
    }


    public void startArbitrator() {
        System.out.println(LogTag + "# Starting the tournament manager now.");
        this.terminate = false;
    }

    public void endArbitrator() {
        this.terminate = true;
    }
}
