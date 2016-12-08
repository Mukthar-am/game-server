package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 * app = {"name":"ConnectTheClocks","build":110107,"version":"3.2.0-QA-MA-test"}
 */
public class App {

    private String name;
    private int buildId;
    private String buildVersion;

    App(String name, int bid, String bversion) {
        this.name = name;
        this.buildId = bid;
        this.buildVersion = bversion;
    }

    public String getName() { return this.name; }
    public int getBuildId() { return this.buildId; }
    public String getBuildVersion() { return this.buildVersion; }

    public String toString() {
        StringBuilder objDetails = new StringBuilder("App=(");
        objDetails.append("Name=" + this.name + ",");
        objDetails.append("BuildId=" + String.valueOf(this.buildId) + ",");
        objDetails.append("BuildVersion=" + this.buildVersion+ ")");

        return objDetails.toString();
    }
}
