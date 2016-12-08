package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 *
 * network = {"bandwidth":"320.8626439680672","bandwidth_bucket":"MODERATE",
 * "carrier":"Vodafone IN","ip":"10.0.6.174","type":"WIFI"}
 */
public class Network {
    private String carrier;
    private String ip;
    private String type;

    public Network(String carrier, String ip, String type) {
        this.carrier = carrier;
        this.ip = ip;
        this.type = type;
    }

    public String getCarrier() { return this.carrier; }
    public String getIp() { return this.ip; }
    public String getType() { return this.type; }

    public String toString() {
        StringBuilder objDetails = new StringBuilder("Network=(");
        objDetails.append("Carrier=" + this.carrier + ",");
        objDetails.append("IP=" + this.ip + ",");
        objDetails.append("Type=" + this.type+ ")");

        return objDetails.toString();
    }
}
