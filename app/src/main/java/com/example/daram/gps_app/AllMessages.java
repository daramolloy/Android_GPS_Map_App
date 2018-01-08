package com.example.daram.gps_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllMessages extends AppCompatActivity {

    public ArrayList<String> MainList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    MainList.add(String.valueOf(messageSnapshot.getKey()) + ", " + (String.valueOf(messageSnapshot.getValue())));
                }

                final TextView[] myTextViews = new TextView[MainList.size()]; // create an empty array;

                for (String i : MainList) {
                    Log.d("Loggg", "Value is: " + i);
                    String ss[] = i.split(",");
                    String Millis = ss[0];
                    String latt = ss[1];
                    String longi = ss[3];
                    String message = ss[2];

                    latt = latt.replace("{", "");
                    longi = longi.replace("}", "");
                    message = message.substring(9);
                    latt = latt.substring(10);
                    longi = longi.substring(11);

                    double latitudeDB = Double.parseDouble(latt);
                    double longitudeDB = Double.parseDouble(longi);

                    Date date = new Date(Long.parseLong(Millis));
                    String dateString = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

<<<<<<< HEAD
                    Log.d("Latt", "Value is: " + latitudeDB);
                    Log.d("Long", "Value is: " + longitudeDB);
                    Log.d("Date", "Value is: " + date);
                    Log.d("Message", "Value is: " + message);



                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

                    TextView textView1 = new TextView(this);
                    textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView1.setText("programmatically created TextView1");
                    textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
                    textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
                    linearLayout.addView(textView1);

//                    // create a new textview
//                    final TextView rowTextView = new TextView(this);
//
//                    // set some properties of rowTextView or something
//                    rowTextView.setText("This is row #" + i);
//
//                    // add the textview to the linearlayout
//                    myLinearLayout.addView(rowTextView);
//
//                    // save a reference to the textview for later
//                    myTextViews[i] = rowTextView;
=======

//                        // create a new textview
//                        final TextView rowTextView = new TextView(this);
//
//                        // set some properties of rowTextView or something
//                        rowTextView.setText("This is row #" + i);
//
//                        // add the textview to the linearlayout
//                        myLinearLayout.addView(rowTextView);
//
//                        // save a reference to the textview for later
//                        myTextViews[i] = rowTextView;
>>>>>>> 521f96529f6dde48e1bea7dd0cc4e37127e547fc


                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Lawg", "Failed to read value.", error.toException());
            }
        });
    }
}
