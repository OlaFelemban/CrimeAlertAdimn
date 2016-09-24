package com.example.ola.crimealertadimn;

import java.text.NumberFormat;

/**
 * Created by ola on 9/23/16.
 */

public class NewEvent {

    // declare the variable to get the input from it
    private String ALert;
    private String Details;
    private String City;
    private double LocationLat;
    private double LocationLon;

    public NewEvent() {
    }

    public String getALert() {
        return ALert;
    }

    public void setALert(String ALert) {
        this.ALert = ALert;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }


    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public double getLocationLat() {
        return LocationLat;
    }

    public void setLocationLat(double locationLat) {
        LocationLat = locationLat;
    }

    public double getLocationLon() {
        return LocationLon;
    }

    public void setLocationLon(double locationLon) {
        LocationLon = locationLon;
    }
}
