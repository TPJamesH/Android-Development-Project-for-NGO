package com.example.assessment2;

import java.util.Date;

public class Site {

    private String id;
    private double lat;
    private double lon;

    private String description;

    private String date;

    private int goal;

    public Site(){
        this.id = "";
        this.lat = 0.0;
        this.lon = 0.0;
        this.description = "";
        this.date = "";
        this.goal = 0;
    }

    public Site(String id,double lat, double lon, String description, String date, int goal) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.date = date;
        this.goal = goal;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
}
