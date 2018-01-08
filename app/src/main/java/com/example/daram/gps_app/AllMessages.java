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

import static android.R.id.list;

public class AllMessages extends AppCompatActivity {

    private ArrayList<String> MainList = new ArrayList();
    private ArrayList<String> MessagesList = new ArrayList();
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
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    MainList.add(String.valueOf(messageSnapshot.getKey()) + ", " + (String.valueOf(messageSnapshot.getValue())));
                }

                for (String i : MainList) {

                    String ss[] = i.split(",");
                    String Millis = ss[0];
                    String message = ss[1];
                    String longi = ss[3];
                    String latt = ss[2];

                    latt = latt.replace("{", "");
                    longi = longi.replace("}", "");
                    message = message.substring(10);
                    latt = latt.substring(10);
                    longi = longi.substring(11);

                    double latitudeDB = Double.parseDouble(latt);
                    double longitudeDB = Double.parseDouble(longi);

                    Date date = new Date(Long.parseLong(Millis));
                    String dateString = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(date);

                    MessagesList.add(dateString + " : " + message);

                }

                arrAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,MessagesList){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                listV.setAdapter(arrAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Lawg", "Failed to read value.", error.toException());
            }
        });

    }
}
