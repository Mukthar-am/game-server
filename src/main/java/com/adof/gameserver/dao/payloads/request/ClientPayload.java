package com.adof.gameserver.dao.payloads.request;

import com.adof.gameserver.utils.generic.ParseUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by 15692 on 23/06/16.
 *
 * Capture the payload for further on-the-fly usage
 *
 app = {"name":"ConnectTheClocks","build":110107,"version":"3.2.0-QA-MA-test"}
 device = {"advertising_id":"675438d9-1c4b-456f-a604-d82c46a69552","build_number":"cancro","category":"Mobile","device_id":"6458b202055249a6","device_year":2013,"device_year_type":2,"height":1920,"imei":"865072028292640","installation_id":"2c721379-3b4e-34f0-a847-e3e8183f835b","manufacturer":"Xiaomi","model_number":"MI 3W","os":"Android","os_api_version":19,"os_version":"4.4.4","width":1080}
 user = {"customer_id":"test@adof360.com","is_logged_in":true,"uidx":"0bbc146c.f825.48d2.8557.9d5150abfaab2IAIxITest"}
 geo = {"city":"Bengaluru","country":"India","lat":12.90124785,"long":77.61674922,"state":"Karnataka"}
 payload = {"custLogin":"test@adof360.com","category":"loadTest","isLoggedIn":true,"action":"loadTest","uidx":"0bbc146c.f825.48d2.8557.9d5150abfaab2IAIxIevLg","eventName":"loadTest","label":"app-launch","installationID":"2c721379-3b4e-34f0-a847-e3e8183f835b","ruLoginID":"loadtest@myntra360.com","offset":-196,"abTest":{"rn.update":"internal"}}
 network = {"bandwidth":"320.8626439680672","bandwidth_bucket":"MODERATE","carrier":"Vodafone IN","ip":"10.0.6.174","type":"WIFI"}
 session = {"session_id":"b6173d32-0174-4204-b0fb-fd1b83b79c3e-6458b2020552MaTouchloadTest","start_time":1460527812898}
 _t = "PurchaseCoins"
 time_stamp = 1460527812970

 */

public class ClientPayload {
    private String EVENT_NAME = null;
    private Date TIME_STAMP = null;
    private String SESSION_ID = null;

    private App APP = null;
    private Device DEVICE = null;
    private Network NETWORK = null;
    private Geo GEO = null;
    private User USER = null;
    private ActionPayload actionPAYLOAD = null;

    public ActionPayload getPayload() { return this.actionPAYLOAD; }
    public User getUser() { return this.USER; }
    public Geo getGeo() { return this.GEO; }
    public Network getNetwork() { return this.NETWORK; }
    public Device getDevice() { return this.DEVICE; }
    public App getApp() { return this.APP; }

    public String getSessionId() { return this.SESSION_ID; }
    public String getEventName() { return this.EVENT_NAME; }
    public Date getPayloadTimeStamp() { return this.TIME_STAMP; }

    public String toString() {
        StringBuilder payloadDetails = new StringBuilder("ActionPayload=(");
        payloadDetails.append(EVENT_NAME + ",");
        payloadDetails.append(SESSION_ID + ",");
        payloadDetails.append(TIME_STAMP + ",");
        payloadDetails.append(APP.toString() + ",");
        payloadDetails.append(DEVICE.toString() + ",");
        payloadDetails.append(NETWORK.toString() + ",");
        payloadDetails.append(GEO.toString() + ",");
        payloadDetails.append(USER.toString() + ")");

        return payloadDetails.toString();
    }


    public ClientPayload parseAndGet(String requestBodyJson) {
        parse(requestBodyJson);
        return this;
    }

    private void parse(String requestBodyJson) {
        JsonNode inputRequest = ParseUtils.stringToJsonNode(requestBodyJson);

        Iterator<String> requestBodyFields = inputRequest.fieldNames();
        while ( requestBodyFields.hasNext() ) {
            String field = requestBodyFields.next();

            switch (field) {
                case "_t":
                    this.EVENT_NAME = inputRequest.get("_t").toString();

                case "session":
                    this.SESSION_ID = inputRequest.get("session").toString();
                    break;

                case "time_stamp":
                    // "2012-06-20 16:00:47"
                    String dtValue = inputRequest.get("time_stamp").toString();
                    Date datetime = null;
                    try {
                        datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .parse(dtValue.substring(1, dtValue.length()-1));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    this.TIME_STAMP = datetime;
                    break;

                case "app":
                    Iterator<String> appParameters = inputRequest.get("app").fieldNames();

                    String name, version;
                    name = version = null;
                    int buildid = 0;

                    while (appParameters.hasNext()) {
                        String appParam = appParameters.next();
                        if (appParam.equalsIgnoreCase("name")) {
                            name = inputRequest.get("app").get(appParam).toString();
                        }
                        else if (appParam.equalsIgnoreCase("build")) {
                            buildid = Integer.parseInt(inputRequest.get("app").get(appParam).toString());
                        }
                        else if (appParam.equalsIgnoreCase("version")) {
                            version = inputRequest.get("app").get(appParam).toString();
                        }
                    }
                    this.APP = new App(name, buildid, version);
                    break;

                case "device":
                    String advertising_id, build_number, category, device_id,
                    imei, manufacturer, model_number, os, os_api_version, os_version;

                    advertising_id = build_number = category = device_id
                            = imei = manufacturer = model_number = os = os_api_version = os_version = null;

                    int width, height;
                    width = height = 0;

                    Iterator<String> deviceParameters = inputRequest.get("device").fieldNames();

                    while (deviceParameters.hasNext()) {
                        String deviceParam = deviceParameters.next();

                        if (deviceParam.equalsIgnoreCase("advertising_id")) {
                            advertising_id = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("build_number")) {
                            build_number = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("category")) {
                            category = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("device_id")) {
                            device_id = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("width")) {
                            width = Integer.parseInt(inputRequest.get("device").get(deviceParam).toString());
                        }
                        else if (deviceParam.equalsIgnoreCase("height")) {
                            height = Integer.parseInt(inputRequest.get("device").get(deviceParam).toString());
                        }
                        else if (deviceParam.equalsIgnoreCase("imei")) {
                            imei = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("manufacturer")) {
                            manufacturer = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("model_number")) {
                            model_number = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("os")) {
                            os = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("os_api_version")) {
                            os_api_version = inputRequest.get("device").get(deviceParam).toString();
                        }
                        else if (deviceParam.equalsIgnoreCase("os_version")) {
                            os_version = inputRequest.get("device").get(deviceParam).toString();
                        }
                    }

                    this.DEVICE = new Device( advertising_id, build_number, category, device_id, height,
                            width, imei, manufacturer, model_number, os, os_api_version, os_version);

                    break;

                case "user":
                    String user_name, email_id, fb_id, gender, uuid;
                    uuid = user_name = email_id = fb_id = gender = null;
                    int age = 0;

                    Iterator<String> userParameters = inputRequest.get("user").fieldNames();

                    while (userParameters.hasNext()) {
                        String userParam = userParameters.next();

                        switch (userParam) {
                            case "uuid":
                                uuid = inputRequest.get("user").get(userParam).toString();
                                break;

                            case "name":
                                email_id = inputRequest.get("user").get(userParam).toString();
                                break;

                            case "email_id":
                                email_id = inputRequest.get("user").get(userParam).toString();
                                break;

                            case "fb_id":
                                fb_id = inputRequest.get("user").get(userParam).toString();
                                break;

                            case "age":
                                age = Integer.parseInt(inputRequest.get("user").get(userParam).toString());
                                break;

                            case "gender":
                                gender = inputRequest.get("user").get(userParam).toString();
                                break;

                            default:
                                break;
                        }
                    }

                    this.USER = new User(Integer.parseInt(uuid), user_name, email_id, fb_id, age, gender);
                    break;

                case "geo":
                    String city, country, state;
                    city = country = state = null;

                    double lat = 0, lon = 0;

                    Iterator<String> geoParameters = inputRequest.get("geo").fieldNames();
                    while (geoParameters.hasNext()) {
                        String geoField = geoParameters.next();
                        String fieldValue = inputRequest.get("geo").get(geoField).toString();
                        switch (geoField) {
                            case "city":
                                city = fieldValue;
                                break;

                            case "country":
                                country = fieldValue;
                                break;

                            case "state":
                                state = fieldValue;
                                break;

                            case "lat":
                                lat = Double.parseDouble(fieldValue);
                                break;

                            case "lon":
                                lon = Double.parseDouble(fieldValue);
                                break;

                            default:
                                break;
                        }
                    }

                    this.GEO = new Geo(city, state, country, lat, lon);

                    break;

                case "payload":
                    /** total_coins, purchased_coins, coins_won, redeemed_coins, earned_coins, utilised_coins,
                     * claim_bonus_coins,
                     * start_tournament, end_tournament, play_tournament
                     * */
                    String payload_category = null;

                    /** Possible values = video_ad , app_download, banner) */
                    String coinsSrc = null;
                    int coins = 0;

                    Date timeStamp = null; /** yyyy-MM-dd HH:mm:ss */
                    double spent = 0;
                    int tournamentId = 0;
                    int gameLevelId = 0;
                    int gameCurrentScore = 0;


                    Iterator<String> payloadParameters = inputRequest.get("payload").fieldNames();
                    while (payloadParameters.hasNext()) {
                        String payloadField = payloadParameters.next();
                        String fieldValue = inputRequest.get("payload").get(payloadField).toString();
                        switch (payloadField) {
                            case "category":
                                payload_category = fieldValue;
                                break;

                            case "coins":
                                coins = Integer.parseInt(fieldValue);
                                break;

                            case "spent":
                                spent = Double.parseDouble(fieldValue);
                                break;

                            case "coins_src":
                                coinsSrc = fieldValue;
                                break;

                            case "tid":
                                tournamentId = Integer.parseInt(fieldValue);
                                break;

                            case "score":
                                gameLevelId =
                                        Integer.parseInt(String.valueOf(inputRequest.get("payload").get(payloadField).get("level_id")));
                                gameCurrentScore =
                                        Integer.parseInt(String.valueOf(inputRequest.get("payload").get(payloadField).get("score")));

                                break;

                            case "time_stamp":
                                String gamePayloadTS = inputRequest.get("payload").get(payloadField).toString();
                                try {
                                    timeStamp =
                                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                    .parse(gamePayloadTS.substring(1, gamePayloadTS.length()-1) );
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                break;


                            default:
                                System.out.println("# WARNING: ActionPayload validation reached default switch-case");
                                break;

                        }
                    }


                    actionPAYLOAD = new ActionPayload(payload_category, coinsSrc, coins, spent, tournamentId, gameLevelId, gameCurrentScore, timeStamp);

                    break;

                case "network":
                    String carrier, ip, type;
                    carrier = ip = type = null;

                    Iterator<String> networkParameters = inputRequest.get("network").fieldNames();
                    while (networkParameters.hasNext()) {
                        String networkField = networkParameters.next();
                        String fieldValue = inputRequest.get("network").get(networkField).toString();

                        switch (networkField) {
                            case "carrier":
                                carrier = fieldValue;
                                break;

                            case "ip":
                                ip = fieldValue;
                                break;

                            case "type":
                                type = fieldValue;
                                break;

                            default:
                                break;
                        }


                    }

                    this.NETWORK = new Network(carrier, ip, type);

                    break;

                default: ;
                    System.out.println("# WARNING: Unhandled field - " + field);
                    break;
            }

        }

    }



}
