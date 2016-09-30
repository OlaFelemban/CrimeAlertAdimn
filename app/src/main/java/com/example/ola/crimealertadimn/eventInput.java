package com.example.ola.crimealertadimn;

import android.content.Intent;
import android.icu.text.TimeZoneFormat;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.location.Address;


import java.io.IOException;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class eventInput extends AppCompatActivity implements View.OnClickListener {

    private EditText Alert;
    private EditText Details;
    private TextView LocationLon;
    private TextView LocationLat;
    private EditText city;
    private Button buttonSave;
    private Button buttonloc;
    private EditText address;


    //add reference to database to connect to database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference EventRef = mRootRef.child("Events");
    // DatabaseReference mAlertRef = mRootRef.child("Alert");
    //DatabaseReference mDetailsRef = mRootRef.child("Details");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_input);


        //Get UI elements
        Alert = (EditText) findViewById(R.id.editTextAlert);
        Details = (EditText) findViewById(R.id.editTextDetails);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        LocationLon = (TextView) findViewById(R.id.editTextLon);
        LocationLat = (TextView) findViewById(R.id.editTextLat);
        city = (EditText) findViewById(R.id.editTextCity);
        buttonloc = (Button) findViewById(R.id.buttonloc);
        address = (EditText) findViewById(R.id.editAdress);


        Intent intent = getIntent();
        LocationLat.setText(intent.getStringExtra("Lat"));
        LocationLon.setText(intent.getStringExtra("Lon"));
        Alert.setText(intent.getStringExtra("Alert"));
        city.setText(intent.getStringExtra("City"));
        Details.setText(intent.getStringExtra("Details"));
        address.setText(intent.getStringExtra("Address"));
        address.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() ==""){
                    LocationLat.setText("");
                    LocationLon.setText("");
                    return;
                }
                searchLoc(s.toString());

            }
        });

        buttonSave.setOnClickListener(this);
        buttonloc.setOnClickListener(this);
    }


    protected void onStart() {
        super.onStart();

        EventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String text =dataSnapshot.getValue(String.class);
                //ArrayList<> text1 = dataSnapshot.getValue(ArrayList.class);
                //Alert.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == buttonSave) {
            String AText = Alert.getText().toString().trim();
            String DText = Details.getText().toString().trim();
            String CText = city.getText().toString().trim();
            double LocLat = Double.parseDouble(LocationLat.getText().toString());
            double LocLon = Double.parseDouble(LocationLon.getText().toString());

            //create new object to push into database
            NewEvent newEvent = new NewEvent();
            newEvent.setALert(AText);
            newEvent.setDetails(DText);
            newEvent.setCity(CText);
            newEvent.setLocationLat(LocLat);
            newEvent.setLocationLon(LocLon);

            // added to database
            EventRef.child("Events").push().setValue(newEvent);


            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

            // Alert.getText().clear();
            //Details.getText().clear();
        }

        if (v == buttonloc) {
            finish();
            Intent intent = new Intent(getApplicationContext(), SelectLocationOnMapActivity.class);
            intent.putExtra("Lat", LocationLat.getText().toString());
            intent.putExtra("Lon", LocationLon.getText().toString());
            intent.putExtra("Alert", Alert.getText().toString());
            intent.putExtra("City", city.getText().toString());
            intent.putExtra("Details", Details.getText().toString());
            intent.putExtra("Address", address.getText().toString());

            startActivity(intent);
        }


    }

    public void searchLoc(String addr) {


        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(addr, 1);
            if (addresses.size() > 0) {
                LocationLon.setText(addresses.get(0).getLongitude() + "");
                LocationLat.setText(addresses.get(0).getLatitude() + "");
                Log.d("RESULT LOC","Result Lat+ Lon: "+addresses.get(0).getLongitude()+"   "+addresses.get(0).getLatitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
