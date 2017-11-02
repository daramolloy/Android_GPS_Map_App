package com.example.daram.gps_app;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class SubmitMessage extends AppCompatActivity implements android.location.LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_message);

//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean canGetLocation = true;
//        int ALL_PERMISSIONS_RESULT = 101;
//        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;// Distance in meters
//        long MIN_TIME_BW_UPDATES = 60000;// Time in milliseconds
//
//        ArrayList<String> permissions = new ArrayList<>();
//        ArrayList<String> permissionsToRequest;
//
//        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
//
//        if (ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//            Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
//
//
//            return;
//        }
//
//        if (canGetLocation) {
//            if (isGPS) {
//                lm.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//            } else if (isNetwork) {
//                // from Network Provider
//
//                lm.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//            }
//        }
//        else{
//            Toast.makeText(this, "Can't get location", Toast.LENGTH_SHORT).show();
//        }
    }

    public void SubmitText(View view) {
        try {
            Location currentLocation = getLocation();
            System.out.println(currentLocation.getLatitude() + " , " + currentLocation.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        Location location = null;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;// Distance in meters
        long MIN_TIME_BW_UPDATES = 60000;// Time in milliseconds
        try {
            LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//            if (ActivityCompat.checkSelfPermission(this,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED) {
//
//                Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
//
//                return null;
//            }

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            lat = location.getLatitude();
//                            lng = location.getLongitude();
//                        }
                    }
                }
                //get the location by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                lat = location.getLatitude();
//                                lng = location.getLongitude();
//                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void Back(View view) {
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
