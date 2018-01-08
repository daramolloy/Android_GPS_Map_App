package com.example.daram.gps_app;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SubmitMessage extends AppCompatActivity implements android.location.LocationListener {

    EditText editText;
    private LocationData currentLoc;
    private Location currentLocation = new Location("");
    private ArrayList<String> LocationStringList = new ArrayList<String>();
    private ArrayList<Location> LocationObjList = new ArrayList<Location>();
    private String addToMsg;
    private String keyToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_message);
        editText = (EditText) findViewById(R.id.editText2);
        startGettingLocations();

    }

    public void SubmitText(View view) {


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefDown = database.getReference();

        myRefDown.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // The currentLoc LocationData object must be parsed as a location object
                    // This is to compare it with each location object in the database using the "distanceTo()" method
                    currentLocation.setLatitude(currentLoc.getLatitude());
                    currentLocation.setLongitude(currentLoc.getLongitude());

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        Log.d("log value", "" + messageSnapshot.getValue().toString());
                        Log.d("log key", "" + messageSnapshot.getKey());
                        String databaseLocations = messageSnapshot.getValue().toString() + "," + messageSnapshot.getKey();
                        Log.d("log locations" , databaseLocations);
                        LocationStringList.add(databaseLocations);
                    }

                    for (String i : LocationStringList) {

                        String ss[] = i.split(",");
                        String message = ss[0];
                        String longi = ss[2];
                        String latt = ss[1];
                        String key = ss[3];

                        latt = latt.replace("{", "");
                        longi = longi.replace("}", "");
                        message = message.substring(9);
                        latt = latt.substring(10);
                        longi = longi.substring(11);

                        double latitudeDB = Double.parseDouble(latt);
                        double longitudeDB = Double.parseDouble(longi);

                        Location compareLoc = new Location("");
                        compareLoc.setLatitude(latitudeDB);
                        compareLoc.setLongitude(longitudeDB);

                        LocationObjList.add(compareLoc);

                        for(Location a : LocationObjList){
                            if ((a.distanceTo(currentLocation))<10){
                                keyToDelete = key;
                                addToMsg = message;
                            }
                        }


                        Log.d("Latt", "Value is: " + latitudeDB);
                        Log.d("Long", "Value is: " + longitudeDB);
                        Log.d("Message", "Value is: " + message);
                    }


                }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Lawg", "Failed to read value.", error.toException());
            }
        });

            // A permission check is required for the getLastKnownLocation() method to work
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              //  return;
            //}


        Date now = new Date();
        String ID = (String.valueOf(now.getTime()));

        for(Location a : LocationObjList){
            float distance = a.distanceTo(currentLocation);
            if (distance>=10){

                DatabaseReference myRefUp = database.getReference(ID);
                MessageData currentData = new MessageData(currentLoc.getLatitude(),
                        currentLoc.getLongitude(), editText.getText().toString());
                myRefUp.setValue(currentData);
            }else{ // Messages closer than 10m must append the old one
                Log.d("key", keyToDelete);

                DatabaseReference refToDelete = database.getReference(keyToDelete);
                refToDelete.removeValue();

                DatabaseReference myRefUp = database.getReference(ID);
                String newMessage = addToMsg + "; " + editText.getText().toString();

                MessageData currentData = new MessageData(currentLoc.getLatitude(),
                        currentLoc.getLongitude(), newMessage);
                myRefUp.setValue(currentData);

            }
            Log.d("distance", "" + distance);
        }
            // Show the user that the message sent successfully
            Context context = getApplicationContext();
            CharSequence text = "Message sent";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }


    public void Back(View view) {
        finish();
    }

    @Override
    public void onLocationChanged(Location location){


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

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private void startGettingLocations() {

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 100;// Time in milliseconds
        Criteria criteria = new Criteria();

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();


            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));
                currentLoc = new LocationData(location.getLatitude(),location.getLongitude());

            } else if (isNetwork) {
                // from Network Provider

                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Location location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria, false));
                currentLoc = new LocationData(location.getLatitude(),location.getLongitude());

            }
        }
        else{
            Toast.makeText(this, "Can't get location", Toast.LENGTH_SHORT).show();
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
