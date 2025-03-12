package com.example.assessment2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.assessment2.databinding.ActivityMainForUserBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main_For_User extends AppCompatActivity implements OnMapReadyCallback,
        TaskLoadedCallback,
        GoogleMap.OnInfoWindowClickListener,
        AdapterView.OnItemSelectedListener {


    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter adapter;
    final Handler ha = new Handler(); //Handler for the method calling
    private final DBase database = new DBase();
    protected LocationRequest mLocationRequest;
    protected FusedLocationProviderClient client;
    int delay = 500; //0.5 secs
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainForUserBinding binding;
    private ListView eventsLV;
    private int finalID_site;
    private ArrayList<Event> eventsList = new ArrayList<>();

    private final ArrayList<Event> events_marker = database.getEventList();
    private String username;
    private LocationManager locationManager;
    private MapView mapView;
    private GoogleMap mMap;
    private Location currentLocation;

    private Marker[] markers;
    private Polyline currentPolyline;

    private Site temp_loc = new Site();


    private String chosen_critera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsList.addAll(database.getEventList());
        binding = ActivityMainForUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client =
                LocationServices.getFusedLocationProviderClient(this);
        requestPermission();

        Button return_to_menu = findViewById(R.id.return_to_menu);
        Button search = findViewById(R.id.searchButton);
        switchList();
        getLastLocation();
        ////////////////////////////
        EditText searchBar = findViewById(R.id.searchBar);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("ID");
        categories.add("Description");
        categories.add("Date");
        categories.add("Goal");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching dataAdapter to spinner
        spinner.setAdapter(dataAdapter);
        ///////////////////////////////////////////

        spinner.setOnItemSelectedListener(this);
        try {
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        eventsLV = findViewById(R.id.list);

        adapter = new CustomAdapter(eventsList, getApplicationContext());

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        Button switch_to_map = findViewById(R.id.switch_map);

        switch_to_map.setOnClickListener(v -> switchMap());

        ///////////////////////////

        Button switch_to_list = findViewById(R.id.switch_list);

        switch_to_list.setOnClickListener(v -> switchList());

        ////////////////////////////////////////
        eventsLV.setAdapter(adapter);
        initializeView();

        return_to_menu.setOnClickListener(v->{
            Intent intent = new Intent(this, User_Choice.class);
            intent.putExtra("message", "Welcome "+ username);
            startActivity(intent);

        });

        search.setOnClickListener(v->{
            adapter.clear();
            if(searchBar.getText().toString().isEmpty()){
                initializeView();
            }
            else{
                initializeView_Search(searchBar.getText().toString(),chosen_critera);
            }
        });





    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        chosen_critera = parent.getItemAtPosition(position).toString();

    }
    public void onNothingSelected(AdapterView<?> arg0) {


    }

    public void onResume() {
        super.onResume();
        mapView.onResume();

    }


    public void initializeView() {
        adapter.clear();
        final int[] i = {0};
        final boolean[] check = {false};
        database.getEventBase().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Event temp = new Event();
                Site temp_site = new Site();
                LocationOwner temp_owner = new LocationOwner();

                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    if (i[0] == 0) {

                        temp_owner.setID(Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getID());

                        temp_owner.setName((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getName()));

                        temp_owner.setPassword((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getPassword()));

                        temp_owner.setPhoneNumber((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getPhoneNumber()));
                        /////////////////////////////////////////////////////////////
                    }
                    if (i[0] == 1) {
                        temp_site.setDescription(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getDescription());

                        temp_site.setId(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getId());

                        temp_site.setLat(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getLat());

                        temp_site.setLon(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getLon());


                        temp_site.setDate(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getDate());

                        temp_site.setGoal(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getGoal());
                        //////////////////////////////////////////////////////
                    }
                    i[0]++;

                }
                i[0] = 0;


                for (int index = 0; index < events_marker.size(); index++) {
                    if (!Objects.equals(temp_owner.getName(), events_marker.get(index).getOwner().getName())) {
                        check[0] = true; //check if the event already existed or not
                    }

                }

                boolean b = check[0];
                //if event not belong to current user and not already added in the list
                if ((!temp_owner.getName().equals(username) & b)) {
                    temp.setLocation(temp_site);
                    temp.setOwner(temp_owner);

                    eventsList.add(temp);

                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                eventsList.remove(snapshot.getValue(Event.class));

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when we move our
                // child in our database.
                // in our code we are note moving any child.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // this method is called when we get any
                // error from Firebase with error.
            }
        });


    }

    public void initializeView_Search(String prompt, String filter) {

        final int[] i = {0};
        final boolean[] check = {false};
        database.getEventBase().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Event temp = new Event();
                Site temp_site = new Site();
                LocationOwner temp_owner = new LocationOwner();

                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    if (i[0] == 0) {

                        temp_owner.setID(Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getID());

                        temp_owner.setName((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getName()));

                        temp_owner.setPassword((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getPassword()));

                        temp_owner.setPhoneNumber((Objects.
                                requireNonNull(snapShot.getValue(LocationOwner.class)).getPhoneNumber()));
                        /////////////////////////////////////////////////////////////
                    }
                    if (i[0] == 1) {
                        temp_site.setDescription(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getDescription());

                        temp_site.setId(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getId());

                        temp_site.setLat(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getLat());

                        temp_site.setLon(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getLon());


                        temp_site.setDate(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getDate());

                        temp_site.setGoal(Objects.
                                requireNonNull(snapShot.getValue(Site.class)).getGoal());
                        //////////////////////////////////////////////////////
                    }
                    i[0]++;

                }
                i[0] = 0;


                for (int index = 0; index < events_marker.size(); index++) {
                    if (!Objects.equals(temp_owner.getName(), events_marker.get(index).getOwner().getName())) {
                        check[0] = true; //check if the event already existed or not
                    }

                }



                boolean b = check[0];


                switch(filter){
                    case "ID":
                        if ((!temp_owner.getName().equals(username) & b & prompt.equals(temp_site.getId()))) {
                            temp.setLocation(temp_site);
                            temp.setOwner(temp_owner);

                            eventsList.add(temp);

                        }
                        break;
                    case "Description":
                        if ((!temp_owner.getName().equals(username) & b & prompt.equals(temp_site.getDescription()))) {
                            temp.setLocation(temp_site);
                            temp.setOwner(temp_owner);

                            eventsList.add(temp);

                        }
                        break;
                    case "Date":
                        if ((!temp_owner.getName().equals(username) & b & prompt.equals(temp_site.getDate()))) {
                            temp.setLocation(temp_site);
                            temp.setOwner(temp_owner);

                            eventsList.add(temp);

                        }
                        break;
                    case "Goal":
                        if ((!temp_owner.getName().equals(username) & b & prompt.equals(String.valueOf(temp_site.getGoal())))) {
                            temp.setLocation(temp_site);
                            temp.setOwner(temp_owner);

                            eventsList.add(temp);

                        }
                        break;
                    default:
                        initializeView();
                }
                //if event not belong to current user and not already added in the list

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                eventsList.remove(snapshot.getValue(Event.class));

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when we move our
                // child in our database.
                // in our code we are note moving any child.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // this method is called when we get any
                // error from Firebase with error.
            }
        });


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

    private void setVisible(int id, boolean isVisible) {
        View aView = findViewById(id);
        if (isVisible) {
            aView.setVisibility(View.VISIBLE);
        } else {
            aView.setVisibility(View.INVISIBLE);
        }
    }

    private void switchList() {
        setVisible(R.id.mapView2, false);
        setVisible(R.id.switch_list, false);
        setVisible(R.id.list, true);
        setVisible(R.id.switch_map, true);
    }

    private void switchMap() {
        setVisible(R.id.mapView2, true);
        setVisible(R.id.switch_list, true);
        setVisible(R.id.list, false);
        setVisible(R.id.switch_map, false);

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

        mMap = googleMap;
        LatLng myLoc;

        if(currentLocation == null){
            myLoc = new LatLng(5.12, 115.12);
        }
        else{
            myLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        mMap = googleMap;


        mMap.addMarker(new MarkerOptions().position(myLoc).title("Your Place"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //////////////////////////////
        mMap.setOnMarkerClickListener(marker ->
        {
            onMarkerClick(marker,  myLoc);
            return true;
        });
        // startLocationUpdate();
        markers = new Marker[events_marker.size()];
        for (int i = 0; i < events_marker.size(); i++) {
            if (!Objects.equals(events_marker.get(i).getOwner().getName(), username)) {
                markers[i] = createMarker(events_marker.get(i).getLocation().getLat(),
                        events_marker.get(i).getLocation().getLon(),
                        events_marker.get(i).getOwner().getName(),
                        "",
                        R.drawable.trash_resized,
                        events_marker.get(i).getLocation().getDescription(),
                        events_marker.get(i).getLocation().getDate(),
                        String.valueOf(events_marker.get(i).getLocation().getGoal()));
            }
        }


    }

    private void getLastLocation() {
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
        Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(location -> {
            if(location!= null){
                currentLocation = location;

                mapView = findViewById(R.id.mapView2);
                mapView.getMapAsync(Main_For_User.this);
            }
        });
    }

    public void onMarkerClick(Marker marker, LatLng myLocation) {
        marker.showInfoWindow();

        onInfoWindowClick(marker);
        String url = getUrl(myLocation,marker.getPosition(),"driving");
        new FetchURL(Main_For_User.this).execute(url,"driving");
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
        getLastLocation();

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

    public void onLocationChanged(Location location) {
        String message = "Updated location " +
                location.getLatitude() + ", " +
                location.getLongitude();
        LatLng newLoc = new LatLng(location.getLatitude(),
                location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(newLoc).title("New Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID, String description, String date, String goal) {
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(description)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," +dest.longitude;

        String mode = "mode=" +directionMode;

        String parameters = str_origin + "&" + str_dest +"&" + mode;

        String output = "json";

        @SuppressLint("ResourceType") String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +getString(R.string.google_maps_key);
        return url;
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        String name = marker.getTitle();
        int index = 0;
        for(int i = 0 ; i < events_marker.size(); i++){
            assert name != null;
            if(name.equals(events_marker.get(i).getOwner().getName())){
                index = i;
                break;
            }
        }
        AlertDialog dialog = new
                AlertDialog.Builder(this).create();
        dialog.setTitle("Detail information:");
        dialog.setMessage("Description:" + eventsList.get(index).getLocation().getDescription()+ "\n"
                + "Owner: "+ eventsList.get(index).getOwner().getName() + "\n"+
                "Goal: "+ eventsList.get(index).getLocation().getGoal() + "kg\n" +
                "Date: "+ eventsList.get(index).getLocation().getDate());
        dialog.show();
    }
}



