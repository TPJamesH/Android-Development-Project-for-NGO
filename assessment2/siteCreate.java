/*
package com.example.assessment2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assessment2.databinding.ActivitySiteCreateBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class siteCreate extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ActivitySiteCreateBinding binding;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 5000;

    protected LocationRequest mLocationRequest;

    protected FusedLocationProviderClient client;
    private MapView mapView;
    private GoogleMap mMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private LatLng position;

    private String username;

    private Button create;

    private final DBase database = new DBase();

   private EditText dateEditText;
    private EditText goaltitudeEditText;
    private EditText descriptionEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivitySiteCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Button return_to_menu = findViewById(R.id.return_to_menu);
       dateEditText = binding.editTextDate;
        goaltitudeEditText = binding.editTextNumber;
        descriptionEditText = binding.description;
         create = findViewById(R.id.Create);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

        return_to_menu.setOnClickListener(v->{
            Intent intent = new Intent(this, User_Choice.class);
            intent.putExtra("message", "Welcome "+ username);
            startActivity(intent);

        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        requestPermission();
        client =
                LocationServices.getFusedLocationProviderClient(this);
        mMap = googleMap;
// Add a marker in Sydney and move the camera
        LatLng rmit = new LatLng(10.73, 106.69);
        mMap.addMarker(new MarkerOptions().position(rmit).title("RMIT Vietnam"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(latLng-> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng));
            position = latLng;

        });

        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(this, marker.toString(),
                    Toast.LENGTH_SHORT).show();
            return false;
        });
        startLocationUpdate();


        create.setOnClickListener(view -> {
            database.writeNewEvent(descriptionEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    Integer.parseInt(goaltitudeEditText.getText().toString()),
                    position.latitude, position.longitude,username );

            Intent intent3 = new Intent(this, User_Choice.class);
            startActivity(intent3);
            Toast.makeText(this, "SUCCESS EVENT CREATION", Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }


    @SuppressLint({"MissingPermission", "RestrictedApi", "VisibleForTests"})
    private void startLocationUpdate() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        client.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                onLocationChanged(locationResult.getLastLocation());
            }
        }, null);
    }

    public void getPosition(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(location ->
                Toast.makeText(this, location.getLatitude() + "",
                Toast.LENGTH_SHORT).show()
        );



    }
    public void onLocationChanged(Location location){
        String message = "Updated location " +
                location.getLatitude() + ", " +
                location.getLongitude();
        LatLng newLoc = new LatLng(location.getLatitude(),
                location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(newLoc).title("New Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void readFile() throws IOException {
        String filename = "username.txt";

        FileInputStream inputStream;
        try {
            username = "";
            inputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) { //read line by line until end of file
                username = line;
            }
        } catch (IOException ignored) {

        }

    }

    }

 */