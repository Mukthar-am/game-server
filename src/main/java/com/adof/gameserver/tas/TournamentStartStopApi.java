package com.adof.gameserver.tas;

import com.adof.gameserver.dao.tmgr.Tournament;
import com.adof.gameserver.manager.CoinManager;
import com.adof.gameserver.manager.TournamentConcluder;
import com.adof.gameserver.manager.TournamentController;
import com.adof.gameserver.manager.TournamentManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Start the server from its get API http://localhost:8080/tas/tcntrl?startupKey=&flag=start|stop
 */

@RestController
@RequestMapping("tcntrl")
public class TournamentStartStopApi {
    TournamentController tController = null;
    TournamentManager MANAGER = null;
    TournamentConcluder CONCLUDER = null;


    private Thread thread = null;
    private Thread concluderThread = null;

    private String tStartupKey = "adm!nMuks";


    /**
     * If you don't put required = false - param will be required by default.
     */
    @RequestMapping(method = RequestMethod.GET, produces="application/json")
    @ResponseBody
    public StartStopApiResponse track(@RequestParam(value = "user") String user,
                            @RequestParam(value = "apiKey") String startupKey,
                            @RequestParam(value = "tflag") String tflag) {

        System.out.println("\n======================================================");
        System.out.println(":= User: " + user + ", Startup Key: " + startupKey);
        System.out.println("======================================================\n");

        StartStopApiResponse response = new StartStopApiResponse();

        if (startupKey.equals(tStartupKey)) {

            if (tflag.equalsIgnoreCase("start")) {
                System.out.println("= Tournament key matched and will be starting the tournament manager\n");

                //this.tController = TournamentController.getInstance();

                this.MANAGER = TournamentController.getInstance().getManagerInstance();
                this.MANAGER.startTournament();

                thread = new Thread(this.MANAGER);
                thread.start();

                /** Concluder thread start proc */
                this.CONCLUDER = TournamentController.getInstance().getTournamentConcluderInstance();
                this.CONCLUDER.startTournamentConcluder();
                concluderThread = new Thread(this.CONCLUDER);
                concluderThread.start();

                List<Tournament> tournaments = this.MANAGER.getActiveTournaments();
                response.setResponseMessage("Started Tournament.");
                response.setTournamentList(tournaments);

            } else if (tflag.equalsIgnoreCase("stop")) {
                /** send kill signal to thread. */
                if (this.MANAGER != null) {

                    this.MANAGER.endTournament();
                    this.CONCLUDER.endTournamentConcluder();

                    System.out.println("= Tournament stopped successfully...");
                } else {
                    System.out.println(":= There's no tournament running at the moment...");
                }

                response.setResponseMessage("Stopped Tournament.");
            }
            else if (tflag.equalsIgnoreCase("gettours")) {
                if (this.MANAGER != null) {
                    this.MANAGER.healthCheck();

                    List<Tournament> tournamentsList = this.MANAGER.getActiveTournaments();
                    System.out.println("+ Tournaments List: " + tournamentsList.toString());
                    response = new StartStopApiResponse(tournamentsList);

                } else {
                    System.out.println(":= There's no tournament running at the moment...");
                }
            }
            else if (tflag.equalsIgnoreCase("winner")) {
                if (this.MANAGER != null) {
                    this.CONCLUDER.healthCheck();

                } else {
                    System.out.println(":= There's no tournament running at the moment...");
                }
            }
            else if (tflag.equalsIgnoreCase("coinmgr")) {
                TournamentController tournamentController = TournamentController.getInstance();
                CoinManager coinManagerInstance = tournamentController.getCoinManagerInstance();
                if (coinManagerInstance != null) {
                    coinManagerInstance.healthCheck();
                } else {
                    System.out.println(":= There's NO CoinManager instance found.");
                }
            }


            return (response);

        } else {
            System.out.println(":= WARNING: Key mismatch, cannot start tournament.");
            return (new StartStopApiResponse("Bad Request. Key mismatch."));
        }
    }


    private class StartStopApiResponse {
        private String responseMessage = "OK";
        private List<Tournament> tournamentList = new ArrayList<>();

        public StartStopApiResponse() {}
        public StartStopApiResponse(String respMessage) {
            this.responseMessage = respMessage;
        }

        public StartStopApiResponse(List<Tournament> tList) {
            this.tournamentList = tList;
        }

        public StartStopApiResponse(String respMsg, List<Tournament> tList) {
            this.responseMessage = respMsg;
            this.tournamentList = tList;
        }

        public String getresponse_message() { return this.responseMessage; }
        public List<Tournament> gettournament_list() { return this.tournamentList; }

        public void setResponseMessage(String respMsg) { this.responseMessage = respMsg; }

        public void setTournamentList(List<Tournament> list) {
            this.tournamentList = list;
        }
    }

}
