package com.example.ola.crimealertadimn;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectLocationOnMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final String TAG = "SelectLocationOnMap";

    private GoogleMap mMap;
    private String Lat, Lon, Alert, City, Details = null;
    private Button btnSelectLocation;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnSelectLocation = (Button) findViewById(R.id.btn_SelectLocation);
        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), eventInput.class);
                intent.putExtra("Lat", Lat);
                intent.putExtra("Lon", Lon);
                intent.putExtra("Alert", Alert);
                intent.putExtra("City", City);
                intent.putExtra("Details", Details);

                startActivity(intent);
            }
        });


        Intent intent = getIntent();
        Lat = intent.getStringExtra("Lat");
        Lon = intent.getStringExtra("Lon");
        Alert = intent.getStringExtra("Alert");
        City = intent.getStringExtra("City");
        Details = intent.getStringExtra("Details");

        Log.d(TAG, "passed massages" + Lat + " " + Lon + " " + Alert + " " + City + " " + Details);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
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

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(initListener());

        // Location Permission

        if (permissionCheck(android.Manifest.permission.ACCESS_FINE_LOCATION,
                MY_PERMISSIONS_REQUEST_READ_CONTACTS)) {
            Log.d(TAG, "Location permission granted");

            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "LocationEnabled");

        } else {
            // Show rationale and request permission.
            Log.d(TAG, "Location permission not granted");
        }

        Log.d(TAG,Lat+" "+Lon);
        if (null==Lat||null ==Lon||Lat.isEmpty()||Lon.isEmpty()) {
            return;
        }

        LatLng inputLocation = new LatLng(Double.parseDouble(Lat), Double.parseDouble(Lat));
        mMap.addMarker(new MarkerOptions().position(inputLocation).title("Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(inputLocation));




    }

    public GoogleMap.OnMapLongClickListener initListener() {
        GoogleMap.OnMapLongClickListener myLongClickListener = new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
                Lat = latLng.latitude+"";
                Lon = latLng.longitude+"";
            }
        };


        return myLongClickListener;
    }
    /**
     * Check permission/request premission
     *
     * @param permission to be checked
     * @param reqCode    of permision
     * @return permission status
     */
    public boolean permissionCheck(String permission, int reqCode) {
        Log.d(TAG, "permission: " + permission);
        Log.d(TAG, "reqCode: " + reqCode);

        boolean flag = false;
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return requestPermission(permission, reqCode);
        }
    }

    /**
     * request premission
     *
     * @param permission to be checked
     * @param reqCode    of permision
     * @return permission status
     */
    private boolean requestPermission(String permission, int reqCode) {
        ActivityCompat.requestPermissions(this, new String[]{
                        permission},
                reqCode);

        return ContextCompat.checkSelfPermission(this, permission) == 0;
    }

}
