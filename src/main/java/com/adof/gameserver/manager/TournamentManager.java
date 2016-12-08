package com.adof.gameserver.manager;

import com.adof.gameserver.dao.payloads.request.User;
import com.adof.gameserver.dao.tmgr.Tournament;
import com.adof.gameserver.utils.scheduler.TourmanentsMetaJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mukthar on 19/11/16.
 *
 * Heart of the tournament - TournamentManager runnable thread.
 *
 */

public class TournamentManager implements Runnable {
    private String LogTag = "# " + this.getClass().getSimpleName() + ": ";

    private boolean newUserParticipation = false;
    private AtomicInteger dynamicTid = new AtomicInteger();

    private volatile boolean newUserAccepted = false;

    private volatile HashMap<String, String> processData = new HashMap<>();
    private User user = null;

    private volatile boolean terminate = false;
    private volatile boolean tournamentCheck = false;

    private int tid = 0;
    private int tCoins = 0;
    private List<Tournament> tournamentsList = new ArrayList<>();
    private List<Tournament> activeTournaments = Collections.synchronizedList(new ArrayList<>());
    private List<Tournament> completedTournaments = new ArrayList<>();

    @Override
    public void run() {
        tournamentsList = getConfiguredTournaments();
        activeTournaments = tournamentsList;

        while (!terminate) {

            /** consider, new user participation */
            if (newUserParticipation) {
                runTournamentForUser();
            }


            /** Check the status of the tournament */
            if (tournamentCheck) {
                processHealthCheck();
            }


        }

        /** All the tournaments list should be reset */
        tournamentsList = new ArrayList<>();
        activeTournaments = new ArrayList<>();
        completedTournaments = new ArrayList<>();

        System.out.println(LogTag + ":= Tournament thread terminated =:");
    }


    public synchronized void runTournamentForUser() {
        System.out.println(LogTag + "= Tournament Id:" + this.tid
                + ", UserId: " + this.user.getUuid()
                + ", EMail: " + this.user.getEmailId());

        /** Check if the tournament Id exists in the active tournament. */
        Tournament tournamentRequested = findTournament(this.tid);
        if (tournamentRequested == null) {
            System.out.println("\n" + LogTag + "Warning: Could not find the tournament your requested for - " +
                    +this.tid + "\n");

            fillIntoSimilarTournaments(tCoins);
        }
        else {
            int currentTournamentUsers = tournamentRequested.getActiveUsersCount();
            int maxUsers = tournamentRequested.getMaxRequiredUsers();

            /** Check to move the tournament from active to completed if full */
            System.out.println("\n# DEBUG: Curr=" + currentTournamentUsers + ", Max=" + maxUsers + "\n");
            if (currentTournamentUsers == maxUsers - 1) {
                tournamentRequested.addUser(user);
                tournamentRequested.markAsClosed();

                completedTournaments.add(
                        activeTournaments.remove(activeTournaments.indexOf(tournamentRequested))
                );

                createNewTournament(tournamentRequested);
                newUserAccepted = true;

            } else if (currentTournamentUsers < maxUsers - 1) {
                tournamentRequested.addUser(user);
                tournamentRequested.markAsClosed();
                newUserAccepted = true;
            }


            System.out.println("\n" + LogTag + "= Active Tournaments =");
            for (Tournament tournament : activeTournaments) {
                System.out.println(LogTag + "Tid: " + tournament.getTid() + ", ActiveUsers: " + tournament.getActiveUsersCount());
            }
            System.out.println(LogTag + "= Active Tournaments =\n");
        }

        /** Flag out that the user played the tournament */
        this.user.setPlayedTournament();

        /** Reset used variables */
        this.newUserParticipation = false;
        this.user = new User();
        this.tid = 0;

        System.out.println(LogTag + "===== " + newUserAccepted + " - ");
    }



    public void startTournament() {
        System.out.println(LogTag + "# Starting the tournament manager now.");
        this.terminate = false;
    }

    public void endTournament() {
        this.terminate = true;
    }

    public void healthCheck() {
        this.tournamentCheck = true;
    }

    public synchronized void setTid(int tid) {
        this.tid = tid;
    }

    public synchronized List<Tournament> getTournamentsList() {
        return tournamentsList;
    }

    public synchronized List<Tournament> getActiveTournaments() {
        return activeTournaments;
    }

    public synchronized List<Tournament> getCompletedTournaments() {
        return completedTournaments;
    }

    private synchronized List<Tournament> getConfiguredTournaments() {
        System.out.println(LogTag + "# Get tournaments pre configured after reading from DB.");
        List<Tournament> tList = TourmanentsMetaJob.getInstance().getTournamentList();
        dynamicTid.set(tList.size());
        return tList;
    }


    /**
     * Play tournament logic
     */
    public void participate(int tid, User user, int coins) {
        System.out.println(LogTag + "# Participation of new user...");
        this.tid = tid;
        this.user = user;
        this.tCoins = coins;

        /** Coin updated thread to be started here */
        TournamentController.getInstance()
                .getCoinManagerInstance()
                .markUsedCoins(user, coins);

        this.newUserParticipation = true;
    }


    public boolean participationAccepted() {
        return newUserAccepted;
    }

    public void participationReset() {
        newUserAccepted = false;
    }

    public Tournament findTournament(int tid) {
        for (Tournament tournament : activeTournaments) {
            if (tournament.getTid() == tid) {
                return tournament;
            }
        }

        return null;
    }

    public HashMap<String, String> getProcessData() {
        return processData;
    }

    private Tournament hasSimilarTournaments(List<Tournament> activeTournaments, int tCoinsToMatch) {
        /** Check if there is another tour of the same fee and add the user */
        for (Tournament tItem : activeTournaments) {
            if (tItem.getFeeCoins() == tCoinsToMatch
                    && tItem.getActiveUsersCount() < tItem.getMaxRequiredUsers()) {

                return tItem;
            }
        }

        return null;
    }


    private synchronized void createNewTournament(Tournament tournamentRequested) {
        int incrTid = dynamicTid.incrementAndGet();
        System.out.println("\n" + LogTag +"= Incr Tournament Id: " + incrTid);
        Tournament additionalTournament
                = new Tournament(tournamentRequested.getName(),
                incrTid,
                tournamentRequested.getFeeCoins(),
                tournamentRequested.getSession(),
                tournamentRequested.getMinRequiredUsers(),
                tournamentRequested.getMaxRequiredUsers(),
                true,
                tournamentRequested.getTournamentCoinPrize()
        );

        additionalTournament.addUser(user);
        activeTournaments.add(additionalTournament);
        System.out.println(LogTag + "Additional Tournament: " + additionalTournament.toString());

        processData.put("revised_tid", String.valueOf(incrTid));
    }

    private synchronized void fillIntoSimilarTournaments(int tCoins) {
        /** Check if there is another tour of the same fee and add the user */
        Tournament similarTournament
                = hasSimilarTournaments(activeTournaments, tCoins);

        if (similarTournament != null) {
            similarTournament.addUser(user);


            processData.put("revised_tid", String.valueOf(similarTournament.getTid()));
            processData.put("respMsg", "tournamentreadjusted");

            System.out.println(LogTag + "Tid: " + similarTournament.getTid()
                    + ", ActiveUsers: " + similarTournament.getActiveUsersCount());

        }
        System.out.println("\n" + LogTag + "# Readjusted the user to another tournament of similar coins...");
        newUserAccepted = true;
    }


    private void processHealthCheck() {
        System.out.println("\n" + LogTag + "=======================================================");

        String isRunning = "is NOT running";
        String hasNewUser = "has NO new user processing";

        if (!terminate) {
            isRunning = "IS running";
        }
        if (newUserParticipation) {
            hasNewUser = "HAS new user processing";
        }

        System.out.println("# Tournament " + isRunning + " and " + hasNewUser);

        System.out.println(":= List of Active Tournaments:");
        for (Tournament tournament : activeTournaments) {
            System.out.println(LogTag + tournament.toString());
        }

        System.out.println("\n:= List of Completed Tournaments =");
        for (Tournament cTournament : completedTournaments) {
            System.out.println(LogTag + cTournament.toString());
        }
        System.out.println(LogTag + "=======================================================\n");

        tournamentCheck = false;
    }


}
