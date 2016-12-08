package com.adof.gameserver.manager;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.dao.tmgr.Tournament;

import java.util.List;

/**
 * Created by mukthar on 22/11/16.
 */
public class TournamentConcluder implements Runnable {
    private String LogTag = "# " + this.getClass().getSimpleName() + ": ";

    private volatile boolean terminate = false;
    private volatile boolean concluderHealthCheckFlag = false;

    private TournamentManager tManagerInstance = null;

    private User currentUser = null;
    private int tid = 0;
    private int tCoins = 0;
    private boolean newUserParticipation;

    private int gameScore = 0;
    private int gameLevel = 0;

    @Override
    public void run() {
        while (!terminate) {

            if (concluderHealthCheckFlag) {
                concluderHealthCheck();
            }

            if (newUserParticipation) {
                processNewEndTournamentRequest();
            }

        }
        System.out.println(LogTag + ":= Concluder thread terminated =:");
    }


    public void printTournaments(List<Tournament> myList) {
        System.out.println(LogTag + "Begin - isting tournaments with size = " + myList.size());
        for (Tournament tournament : myList) {
            List<User> tUserList = tournament.getActiveUsers();
            for (User user : tUserList) {
                System.out.println(user.toString());
            }
        }
        System.out.println(LogTag + "End - listing tournaments with size = " + myList.size());
    }


    private boolean endTournamentPlaySession(List<Tournament> listOfTournamets) {

        for (Tournament tournament : listOfTournamets) {

            if (tournament.getTid() == tid) {
                /** Get the current currentUser from list of users */
                List<User> tournamentUsers = tournament.getActiveUsers();
                int currentUserUuid = currentUser.getUuid();

                for (User tournamentUser : tournamentUsers) {
                    if ( tournamentUser.getUuid() == currentUserUuid ) {
                        tournamentUser.setGameScore(this.currentUser.getGameScore());
                        tournamentUser.setGameLevel(this.currentUser.getGameLevel());
                        tournamentUser.setConcludedTournament();

                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void startTournamentConcluder() {
        System.out.println(LogTag + "Starting the tournament concluder now.");
        this.terminate = false;
    }

    public void endTournamentConcluder() {
        this.terminate = true;
    }

    public void healthCheck() {
        this.concluderHealthCheckFlag = true;
    }

    /**
     * End tournament logic
     */
    public void endUserTournament(int tid, User user, int coins) {
        System.out.println(LogTag + "# Participation of new currentUser...");
        this.tid = tid;
        this.currentUser = user;
        this.tCoins = coins;

        this.newUserParticipation = true;   /* Flag to wake up the thread */
    }


    private void concluderHealthCheck() {
        System.out.println("\n" + LogTag + "=======================================================");
        System.out.println("# TournamentConcluder run flag = " + terminate
                + ", User participation flag = " + newUserParticipation);

        concluderHealthCheckFlag = false;
    }

    private synchronized void processNewEndTournamentRequest() {
        /** User end tournament call
         * 1. Find the currentUser in completed tournaments
         * 2. IF NOT then find in active tournaments
         * 3. Conclude his gameScore - hacked or not hacked
         * 4. Record his gameScore for tournament updates. */

        /** Get the list of tournaments */
        boolean foundCurrentUser = false;
        tManagerInstance = TournamentController.getInstance().getManagerInstance();
        List<Tournament> completedTournaments = tManagerInstance.getCompletedTournaments();

        /** Method call to end tournament */
        foundCurrentUser = endTournamentPlaySession(completedTournaments);

        if (!foundCurrentUser) {
            System.out.println(LogTag + "Couldn't find in completed tournaments, searching in active tournaments.");
            List<Tournament> activeTournaments = tManagerInstance.getActiveTournaments();
            foundCurrentUser = endTournamentPlaySession(activeTournaments);

            printTournaments(activeTournaments);
        }

        System.out.println(LogTag + "Found the currentUser to map into the tournament : " + foundCurrentUser);

        printTournaments(completedTournaments);
        newUserParticipation = false;
    }
}
