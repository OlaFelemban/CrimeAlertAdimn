package com.example.ola.crimealertadimn;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewEventsOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG="viewEventsOnMap";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference EventRef = mRootRef.child("Events");
    private List<NewEvent> eventList= new ArrayList<NewEvent>();

    private LocationManager locationManager;
    private String provider;// Location provider
    private Location location =  null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //API_KEY
        // AIzaSyB1xoVID79Fvjqi5H_rTapJ_SCFk1tQ9Sk
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreat Started");

        setContentView(R.layout.activity_view_events_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();


        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d(TAG,"firebase user"+ user.getEmail());




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);



                // Getting reference to the TextView to set latitude
                TextView tvAlertEvent = (TextView) v.findViewById(R.id.tv_alert_event);

                // Getting reference to the TextView to set longitude
                TextView tvCity = (TextView) v.findViewById(R.id.tv_city);

                TextView tvDescription = (TextView) v.findViewById(R.id.tv_description);

                Log.d(TAG, "arg0.getPosition():"+arg0.getPosition().toString());
                Log.d(TAG, "Location"+new LatLng(location.getLatitude(),location.getLongitude()).toString());

                double distance =  getDistance(arg0.getPosition(),new LatLng(location.getLatitude(),location.getLongitude()));
                Log.d(TAG, "distance"+distance);

               if(distance<10){
                   v.setBackgroundColor(Color.parseColor("#FF0000"));
                }else if(distance<50){
                   v.setBackgroundColor(Color.parseColor("#F7F709"));
               }else if(distance<100){
                   v.setBackgroundColor(Color.parseColor("#33CC52"));
               }else{
                   v.setBackgroundColor(Color.parseColor("#FFFFFF"));
               }

                String str = arg0.getSnippet();
                String[] strList = str.split("\\|");
                // Setting the latitude
                tvAlertEvent.setText(strList[0]);

                // Setting the longitude
                tvCity.setText(strList[1]);

                tvDescription.setText(strList[2]);
                // Returning the view containing InfoWindow contents
                return v;

            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                return null;

            }
        });
        //call request permission alert

        int tmp = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "C)"+tmp);


        if (permissionCheck(android.Manifest.permission.ACCESS_FINE_LOCATION ,
                MY_PERMISSIONS_REQUEST_READ_CONTACTS)) {
            Log.d(TAG, "Location permission granted");

            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "LocationEnabled");

        } else {
            // Show rationale and request permission.
            Log.d(TAG, "Location permission not granted");
        }
        // initLocation Listener
        initLocationListener();

        //read data from Firebase
        retrieveData();

    }

    private void initLocationListener() {
// 获得LocationManager的实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //优先使用gps
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 没有可用的位置提供器
            Toast.makeText(viewEventsOnMap.this, "没有位置提供器可供使用", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        try{
            int tmp = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            Log.d(TAG, "C)"+tmp);

            location = locationManager.getLastKnownLocation(provider);

        }catch(Exception e){
            Log.w(TAG, "e)"+e);
        }

        if (location != null) {
            Log.d(TAG, "Android location:"+location.toString());
        } else {
            Log.d(TAG, "Android location not avaliable:");
        }
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onLocationChanged(Location location) {
                //TODO I have No Idea What Should I do
            }
        };

        // 更新当前位置
        locationManager.requestLocationUpdates(provider, 10 * 1000, 1,
                locationListener);


    }

    private void addMarkers(List<NewEvent> eventList) {
        Log.d(TAG, "addMarkers Started");

        // muncie 40.204,-85.408
        LatLng camPosition = new LatLng(location.getLatitude(),location.getLongitude());
        for(NewEvent event:eventList){
            Log.d(TAG, "Event:"+event.toString());
           //
           mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLocationLat(),event.getLocationLon()))
                    .title(event.getALert())
                    .snippet(event.getALert()+"|City: "+event.getCity()+"|Description: "+event.getDetails()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(camPosition));

    }
    private void retrieveData() {
        //read alert from firebase
        Log.d(TAG, "retrieveData started");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Map eventMap = new HashMap<String, NewEvent>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, NewEvent>> t = new GenericTypeIndicator<HashMap<String,NewEvent>>() {};
                    eventMap = postSnapshot.getValue(t);
                    Log.d(TAG, "eventMap"+eventMap.toString());
                    for ( Object event : eventMap.values()) {
                       eventList.add((NewEvent) event);

                    }
                    Log.d(TAG, "eventList"+eventList.toString());
                    addMarkers(eventList);
                }
            }
/*
                NewEvent event = dataSnapshot.getValue(NewEvent.class);

                eventList.add(event);
 */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


        EventRef.addValueEventListener(postListener);
    }

    /**
     * Check permission/request premission
     * @param permission to be checked
     * @param reqCode of permision
     * @return permission status
     */
    public boolean permissionCheck(String permission, int reqCode){
        Log.d(TAG, "permission: "+permission);
        Log.d(TAG, "reqCode: "+reqCode);

        boolean flag = false;
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return requestPermission(permission, reqCode);
        }
    }
    /**
     * request premission
     * @param permission to be checked
     * @param reqCode of permision
     * @return permission status
     */
    private boolean requestPermission(String permission, int reqCode) {
        ActivityCompat.requestPermissions( this,new String[] {
                        permission },
                reqCode);

        return ContextCompat.checkSelfPermission(this,permission)==0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO impliment permission result handling
        /*  if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }*/
    }

    public double getDistance(LatLng start,LatLng end){
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        return d;
    }
}
