package com.example.daram.gps_app;


/**
 * Created by daram on 09/10/2017.
 */

public class LocationData {

    private double latitude;
    private double longitude;


    public LocationData(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
}
