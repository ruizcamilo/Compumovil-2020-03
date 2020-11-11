package com.example.taller3.model;

public class LocationT {

    private String name;
    private Double longitude;
    private Double latitude;

    public LocationT(){
        this.name = "";
        this.longitude = 0.0;
        this.latitude = 0.0;
    }

    public LocationT(String name, Double longitude, Double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
