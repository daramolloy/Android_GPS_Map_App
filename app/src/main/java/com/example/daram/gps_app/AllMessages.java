package com.example.daram.gps_app;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AllMessages extends AppCompatActivity {

    //Creating Mainlist to add the raw database values into
    private ArrayList<String> MainList = new ArrayList();
    //Creating MessagesList to add the correctly formatted strings into
    private ArrayList<String> MessagesList = new ArrayList();
    //Initialising the listview and arrayadapter to add the messageslist to the activity
    private ListView listV;
    private ArrayAdapter<String> arrAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        listV = (ListView) findViewById(R.id.lsView);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Adding original data to MainList
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    //Adding key and then value in record, separated by , for delimiting
                    MainList.add(String.valueOf(messageSnapshot.getKey()) + ", " + (String.valueOf(messageSnapshot.getValue())));
                }
                //Formatting of the data and adding to MessagesList
                for (String i : MainList) {
                    //Delimiting original data with ,
                    String ss[] = i.split(",");
                    String Millis = ss[0];
                    String message = ss[1];
                    String longi = ss[3];
                    String latt = ss[2];

                    //Formatting message
                    message = message.substring(10);

                    //Formatting lattitude and longitude values
                    latt = latt.replace("{", "");
                    longi = longi.replace("}", "");
                    latt = latt.substring(10);
                    longi = longi.substring(11);
                    double latitudeDB = Double.parseDouble(latt);
                    double longitudeDB = Double.parseDouble(longi);

                    //Formatting Date
                    Date date = new Date(Long.parseLong(Millis));
                    String dateString = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

                    //Adding formatted string to MessagesList
                    MessagesList.add(dateString + " : " + message);

                }
                //Adding MessagesList to the ListView
                arrAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,MessagesList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        TextView textV = (TextView) view.findViewById(android.R.id.text1);
                        textV.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                listV.setAdapter(arrAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Log", "Failed to read value.", error.toException());
            }
        });

    }

    //Method for Return to Home button
    public void back(View view) {
        finish();
    }
}