package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 *  geo = {"city":"Bengaluru","country":"India","lat":12.90124785,"long":77.61674922,
 *  "state":"Karnataka"}
 */
public class Geo {
    private String city;
    private String country;
    private double lat;
    private double longg;
    private String state;

    public Geo(String city, String state, String country, double lat, double longg) {
        this.city = city;
        this.country = country;
        this.lat = lat;
        this.longg = longg;
        this.state = state;
    }

    public String getCity() { return this.city; }
    public String getCountry() { return this.country; }
    public String getState() { return this.state; }
    public double getLat() { return this.lat; }
    public double getLongg() { return this.longg; }

    public String toString() {
        StringBuilder objDetails = new StringBuilder("Geo=(");
        objDetails.append("City=" + this.city + ",");
        objDetails.append("Country=" + this.country + ",");
        objDetails.append("State=" + this.state+ ",");
        objDetails.append("Lat=" + String.valueOf(this.lat)+ ",");
        objDetails.append("Lang=" + String.valueOf(this.longg) + ")");

        return objDetails.toString();
    }
}

