package com.example.daram.gps_app;


/**
 * Created by daram on 09/10/2017.
 */

public class MessageData {

    private double latitude;
    private double longitude;
    private String message;


    public MessageData(double latitude, double longitude, String message){
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;

    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String getMessage(){
        return this.message;
    }
}
