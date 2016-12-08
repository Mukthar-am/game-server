package com.adof.gameserver.manager;

/**
 * Created by mukthar on 20/11/16.
 *
 * To obtain manager instance.
 */
public class TournamentController {
    private static TournamentController instance = null;
    private TournamentManager managerInstance = null;
    private TournamentConcluder concluderInstance = null;
    private CoinManager coinMgrInstance = null;

    private TournamentController() {
        // Exists only to defeat instantiation.
    }

    public static TournamentController getInstance() {
        if(instance == null) {
            instance = new TournamentController();
        }
        return instance;
    }

    public TournamentManager getManagerInstance() {
        if (managerInstance == null) {
            managerInstance = new TournamentManager();
        }
        return managerInstance;
    }

    public TournamentConcluder getTournamentConcluderInstance() {
        if (concluderInstance == null) {
            concluderInstance = new TournamentConcluder();
        }
        return concluderInstance;
    }


    public CoinManager getCoinManagerInstance() {
        if (coinMgrInstance == null) {
            coinMgrInstance = new CoinManager();
        }
        return coinMgrInstance;
    }

    public void resetTournamentManagerInstance() {
        managerInstance = null;
        managerInstance = new TournamentManager();
    }
}
