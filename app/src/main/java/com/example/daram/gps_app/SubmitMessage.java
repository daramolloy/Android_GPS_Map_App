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

    // Initialise variables
    EditText editText;
    private LocationData currentLoc; // Current GPS LocationData
    private Location currentLocation = new Location(""); // Current GPS Location
    private ArrayList<String> LocationStringList = new ArrayList<String>(); // ArrayList of LocationData parsed as strings
    private ArrayList<Location> LocationObjList = new ArrayList<Location>(); // ArrayList of Location
    private String addToMsg; // Used to append messages within 10m
    private String keyToDelete; // Reference to delete old entries


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_message);
        editText = (EditText) findViewById(R.id.editText2); // EditText object
        startGettingLocations();

    }

    /* OnClick method implemented by the Submit Text button.
    *  When pressed, sends the message in the EditText box to the Firebase database with the
    *  coordinates. Also checks the distance to every other message, and if any are closer
    *  than 10m, they are appended as opposed to a new one being added
    */
    public void SubmitText(View view) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRefDown = database.getReference();

        // The currentLoc LocationData object must be parsed as a location object
        // This is to compare it with each location object in the database using the "distanceTo()" method
        currentLocation.setLatitude(currentLoc.getLatitude());
        currentLocation.setLongitude(currentLoc.getLongitude());

        /* As the activity is finished when the message is sent, we only
        ** need a single event listener, this waits for the data to change
        ** implements once
         */
        myRefDown.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get the current date as an ID reference
                Date now = new Date();
                String ID = (String.valueOf(now.getTime()));

                // Input string cannot have a comma as it will interfere with the delimiter
                String messageToSend = editText.getText().toString();
                messageToSend = messageToSend.replace(","," ");

                // Case where database is null
                if (dataSnapshot.getChildrenCount() == 0){
                    // Create a MessageData object and upload to the database
                    DatabaseReference myRefUp = database.getReference(ID);
                    MessageData currentData = new MessageData(currentLoc.getLatitude(),
                            currentLoc.getLongitude(), messageToSend);
                    myRefUp.setValue(currentData);
                }

                // Get all elements in the database and store them in an arrayList
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String databaseLocations = messageSnapshot.getValue().toString() + "," + messageSnapshot.getKey();
                    LocationStringList.add(databaseLocations);
                }

                // Iterate through the arrayList seperating the text elements
                for (String i : LocationStringList) {

                    // Delimit the elements into substrings using commas as a delimiter
                    String ss[] = i.split(",");
                    String message = ss[0];
                    String longi = ss[2];
                    String latt = ss[1];
                    String key = ss[3];

                    // Format the strings to be parsed as doubles
                    latt = latt.replace("{", "");
                    longi = longi.replace("}", "");
                    message = message.substring(9);
                    latt = latt.substring(10);
                    longi = longi.substring(11);

                    // Parse the lat and long values as doubles
                    double latitudeDB = Double.parseDouble(latt);
                    double longitudeDB = Double.parseDouble(longi);

                    // Create a new location object based on the retrieved latitude and longitude
                    Location compareLoc = new Location("");
                    compareLoc.setLatitude(latitudeDB);
                    compareLoc.setLongitude(longitudeDB);

                    // Add each new Location Object to an arrayList
                    LocationObjList.add(compareLoc);

                    // Iterate through arrayList looking for locations within 10m of each other
                    // If a case is found, we need to save the key and the message
                    for(Location a : LocationObjList) {
                        if ((a.distanceTo(currentLocation)) < 10.0) {
                            keyToDelete = key;
                            addToMsg = message;
                        }
                    }
                }

                for(Location a : LocationObjList){ // Loop through all the Location Objects to check distance between current location

                    float distance = a.distanceTo(currentLocation); // Store the distance between current location and each Location Object

                    if (distance>=10.0){ // If the distance is greater than 10m we can just add a new one

                        DatabaseReference myRefUp = database.getReference(ID);
                        MessageData currentData = new MessageData(currentLoc.getLatitude(),
                                currentLoc.getLongitude(), messageToSend);
                        myRefUp.setValue(currentData); // Add database entry with current value in the editText field

                    }else{ // Messages closer than 10m must append the old one

                        // Remove the old entry
                        // The string associated with the old value is saved in "addToMsg"
                        DatabaseReference refToDelete = database.getReference(keyToDelete);
                        refToDelete.removeValue();

                        DatabaseReference myRefUp = database.getReference(ID); // Get data reference

                        String newMessage = addToMsg + "; " + messageToSend; // Append the message

                        MessageData currentData = new MessageData(currentLoc.getLatitude(),
                                currentLoc.getLongitude(), newMessage);
                        myRefUp.setValue(currentData); // Add new data to database
                    }
                }

                // Show the user that the message sent successfully
                Context context = getApplicationContext();
                CharSequence text = "Message sent";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                finish(); // Finish the activity once the message is sent
            }
        @Override
            public void onCancelled(DatabaseError error) { // Case where no data could be read from the database
            // Failed to read value
            Log.w("Database Error", "Failed to read value.", error.toException());
        }});
    }


    public void Back(View view) { // Finish the activity if the back button is pressed
        finish();
    }

    // Overridden methods needed to be implemented
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

    // Uses the same implementation as MapActivity
    private void startGettingLocations() {

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 10;// Time in milliseconds
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
