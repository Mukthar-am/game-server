package com.adof.gameserver.tas;


import com.adof.gameserver.dao.payloads.request.ClientPayload;
import com.adof.gameserver.dao.payloads.response.ResponsePayload;
import com.adof.gameserver.helpers.GamePlayHelpers;
import org.springframework.web.bind.annotation.*;

/**
 * Created by mukthar on 18/11/16.
 *
 * From CLI:
 * curl -X POST -d @filename.txt http://139.59.46.22:8080/tas/tmgr --header "Content-Type:application/json"
 *
 */
@RestController
@RequestMapping("tmgr")
public class TournamentMgrController {
    private String LogTag = "# [TournamentMgrController]: ";

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json", produces="application/json")
    @ResponseBody
    public ResponsePayload track(@RequestBody String requestBodyJson) {

        ResponsePayload responsePayload = null; /** Play API response body */

        /**
         * play_tournament, start_tournament, end_tournament
         */
        ClientPayload clientPayload = new ClientPayload().parseAndGet(requestBodyJson);
        String category = clientPayload.getPayload().getCategory();
        String action_category = category.substring(1, category.length()-1 );

        GamePlayHelpers gamePlayHelpers = new GamePlayHelpers();

        switch (action_category.toLowerCase()) {
            case "play_tournament":
                responsePayload = gamePlayHelpers.playTournament(clientPayload);

                break;

            case "start_tournament":
                responsePayload = gamePlayHelpers.startTournament(clientPayload);
                break;

            case "end_tournament":
                responsePayload = gamePlayHelpers.endTournament(clientPayload);
                break;

            default:
                System.out.println(LogTag + " WARNING: Invalid action category.");
                break;
        }

        return responsePayload;
    }

}