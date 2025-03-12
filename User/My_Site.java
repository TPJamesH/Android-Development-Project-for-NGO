package com.example.assessment2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.assessment2.databinding.ActivityMySiteBinding;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class My_Site extends AppCompatActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_SMS = 99;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static String MESSAGE_BUTTON;
    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter_Volunteer adapter_volunteer;
    private final DBase database = new DBase();
    private final ArrayList<Event> eventsList = new ArrayList<>();
    private final ArrayList<Event> events_marker = database.getEventList();
    private Volunteer[] volunteerList = new Volunteer[events_marker.size()];

    protected IntentFilter intentFilter;
    protected LocationRequest mLocationRequest;
    protected FusedLocationProviderClient client;
    private ListView eventsLV;
    private ListView volunteerLV;
    private String username;
    private MapView mapView;
    private GoogleMap mMap;
    private LatLng position;

    private Event interestedEvent;

    private Button modify;

    private EditText dateEditText;
    private EditText goaltitudeEditText;
    private EditText descriptionEditText;
    private List<Volunteer> interestedVolunteer;
    final Handler ha = new Handler(); //Handler for the method calling
    int delay = 500; //0.5 secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.assessment2.databinding.ActivityMySiteBinding binding = ActivityMySiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button returnList = findViewById(R.id.return_to_list);
        Button return_to_menu = findViewById(R.id.return_to_menu);
        switchList_Event();

        switchView_Edit_OFF();
        try {
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        eventsLV = findViewById(R.id.my_list);
        volunteerLV = findViewById(R.id.my_volunteer_list);

        dateEditText = binding.editTextDate;
        goaltitudeEditText = binding.editTextNumber;
        descriptionEditText = findViewById(R.id.description_modify);
        modify = binding.Modify;
        Button modify_cancel = binding.quitModify;


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        ////////////////////////////////////////

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);


        ////////////////////////////////
        adapter = new CustomAdapter(eventsList, getApplicationContext());

        ////////////////////////////////////////
        eventsLV.setAdapter(adapter);
        initializeView();

        returnList.setOnClickListener(v -> switchList_Event());

        return_to_menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, User_Choice.class);
            intent.putExtra("message", "Welcome " + username);
            startActivity(intent);

        });

        Toast.makeText(this, "CLICK INTO ONE OF YOUR SITE TO SEE LIST OF VOLUNTEERS OR MODIFY IT", Toast.LENGTH_SHORT).show();


        modify_cancel.setOnClickListener(v -> {
            Intent intent3 = new Intent(this, My_Site.class);
            startActivity(intent3);
            Toast.makeText(this, "RETURNING...", Toast.LENGTH_SHORT).show();
        });

        ha.postDelayed(() -> {
            if(eventsList.size() == 0){
                setVisible(R.id.my_list,false);
                setVisible(R.id.textView6,true);
            }
            else{
                setVisible(R.id.my_list, true);
                setVisible(R.id.textView6,false);
            }
        }, 1000);

    }


    public void onResume() {
        super.onResume();
        mapView.onResume();
////////////////////////////////////////
        eventsLV.setOnItemClickListener((parent, view, position, id) -> {

            Event event = eventsList.get(position);
            interestedEvent = event;
            volunteerList = event.getVolunteers().clone();
            adapter_volunteer = new CustomAdapter_Volunteer(volunteerList, getApplicationContext());
            volunteerLV.setAdapter(adapter_volunteer);

            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle("ALERT");
            dialog.setMessage("What do you want to do");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "See Volunteers", (dialog1, which) -> {
                initializeView_VoLunteer(event.getLocation().getId());
                if(volunteerList[0].getID().isEmpty()){
                    Toast.makeText(this, "NO VOLUNTEER", Toast.LENGTH_SHORT).show();
                    setVisible(R.id.my_list, false);
                    setVisible(R.id.return_to_list, true);
                }
                else {
                    switchList_Volunteer();
                }
            });

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Modify Event", (dialog12, which) -> initializeView_Edit(event));
            dialog.show();
            mapView.getMapAsync((OnMapReadyCallback) this);


        });

    }


    public void initializeView() {
        final int[] i = {0};
        final boolean[] check = {false};
        database.getEventBase().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Event temp = new Event();
                Site temp_site = new Site();
                LocationOwner temp_owner = new LocationOwner();
                Volunteer volunteer = new Volunteer();

                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    if (i[0] == 0) {

                        temp_owner.setID(Objects.requireNonNull(snapShot.getValue(LocationOwner.class)).getID());

                        temp_owner.setName((Objects.requireNonNull(snapShot.getValue(LocationOwner.class)).getName()));

                        temp_owner.setPassword((Objects.requireNonNull(snapShot.getValue(LocationOwner.class)).getPassword()));

                        temp_owner.setPhoneNumber((Objects.requireNonNull(snapShot.getValue(LocationOwner.class)).getPhoneNumber()));
                        /////////////////////////////////////////////////////////////
                    }
                    if (i[0] == 1) {
                        temp_site.setDescription(Objects.requireNonNull(snapShot.getValue(Site.class)).getDescription());

                        temp_site.setId(Objects.requireNonNull(snapShot.getValue(Site.class)).getId());

                        temp_site.setLat(Objects.requireNonNull(snapShot.getValue(Site.class)).getLat());

                        temp_site.setLon(Objects.requireNonNull(snapShot.getValue(Site.class)).getLon());


                        temp_site.setDate(Objects.requireNonNull(snapShot.getValue(Site.class)).getDate());

                        temp_site.setGoal(Objects.requireNonNull(snapShot.getValue(Site.class)).getGoal());
                        //////////////////////////////////////////////////////
                    }
                    if (i[0] == 2) {
                        for (DataSnapshot snapshot_child_volunteer : snapShot.getChildren()) {
                            volunteer.setID(Objects.requireNonNull(snapshot_child_volunteer.getValue(Volunteer.class)).getID());
                            volunteer.setName(Objects.requireNonNull(snapshot_child_volunteer.getValue(Volunteer.class)).getName());
                            volunteer.setPassword(Objects.requireNonNull(snapshot_child_volunteer.getValue(Volunteer.class)).getPassword());
                            volunteer.setPhoneNumber(Objects.requireNonNull(snapshot_child_volunteer.getValue(Volunteer.class)).getPhoneNumber());
                            //////////////////////////////////////////////////////
                        }
                    }
                    i[0]++;

                }
                i[0] = 0;


                for (int index = 0; index < events_marker.size(); index++) {
                    if (Objects.equals(temp_owner.getName(), events_marker.get(index).getOwner().getName())) {
                        check[0] = true;
                    }

                }

                boolean b = check[0];
                //if event belong to current user and not already added in the list
                if ((temp_owner.getName().equals(username) & b)) {
                    temp.setLocation(temp_site);
                    temp.setOwner(temp_owner);
                    temp.addVolunteer(volunteerList, volunteer);

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


    public void initializeView_VoLunteer(String locationID) {
        database.getEventBase().child(locationID).child("Volunteer").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Volunteer temp = new Volunteer();
                temp.setID(Objects.requireNonNull(dataSnapshot.getKey()));
                temp.setName(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getName());

                temp.setPassword(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getPassword());
                temp.setPhoneNumber(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getPhoneNumber());

                addVolunteer(volunteerList, temp);
                adapter_volunteer.notifyDataSetChanged();

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

    public void initializeView_Edit(Event event) {
        dateEditText.setText(event.getLocation().getDate());
        goaltitudeEditText.setText(String.valueOf(event.getLocation().getGoal()));
        descriptionEditText.setText(event.getLocation().getDescription());
        switchView_Edit_ON();
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

    private void switchList_Event() {
        setVisible(R.id.my_volunteer_list, false);
        setVisible(R.id.my_list, true);

        setVisible(R.id.return_to_list, false);
    }

    private void switchList_Volunteer() {
        setVisible(R.id.my_list, false);
        setVisible(R.id.my_volunteer_list, true);

        setVisible(R.id.return_to_list, true);
    }


    private void switchView_Edit_OFF() {
        setVisible(R.id.description_modify, false);
        setVisible(R.id.Modify, false);
        setVisible(R.id.space, false);
        setVisible(R.id.mapView, false);
        setVisible(R.id.editTextDate, false);
        setVisible(R.id.editTextNumber, false);
        setVisible(R.id.quit_modify, false);
    }


    private void switchView_Edit_ON() {
        setVisible(R.id.description_modify,true);
        setVisible(R.id.Modify, true);
        setVisible(R.id.space, true);
        setVisible(R.id.mapView, true);
        setVisible(R.id.editTextDate, true);
        setVisible(R.id.editTextNumber, true);
        setVisible(R.id.quit_modify, true);

        setVisible(R.id.my_list, false);
        setVisible(R.id.return_to_list, false);
        setVisible(R.id.my_volunteer_list, false);

    }



    public void addVolunteer(Volunteer[] arr, Volunteer x) {

        // create a new ArrayList
        List<Volunteer> arrlist = new ArrayList<>(Arrays.asList(arr));

        // Add the new element
        arrlist.add(x);

        // Convert the Arraylist to array
        volunteerList = arrlist.toArray(arr);

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
        requestPermission();
        LatLng location;

        client = LocationServices.getFusedLocationProviderClient(this);
        mMap = googleMap;
// Add a marker in Sydney and move the camera

        if (interestedEvent == null) {
            location = new LatLng(10.73, 106.69);
        } else {
            location = new LatLng(interestedEvent.getLocation().getLat(), interestedEvent.getLocation().getLon());
        }

        mMap.addMarker(new MarkerOptions().position(location).title("Your chosen location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng));
            position = latLng;

        });

        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(this, marker.toString(), Toast.LENGTH_SHORT).show();
            return false;
        });
    //    startLocationUpdate();


        modify.setOnClickListener(view -> {
            if(position == null){
                position = new LatLng(interestedEvent.getLocation().getLat(),
                        interestedEvent.getLocation().getLon());
            }
            database.modifyEvent(descriptionEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    Integer.parseInt(goaltitudeEditText.getText().toString()),
                    position.latitude, position.longitude, username, interestedEvent.getLocation().getId());


            //////////////////////////////////////
            Intent intent3 = new Intent(this, My_Site.class);
            startActivity(intent3);
            Toast.makeText(this, "SUCCESS EVENT MODIFICATION", Toast.LENGTH_SHORT).show();

            switchView_Edit_OFF();

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
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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

    public void onLocationChanged(Location location) {
        String message = "Updated location " + location.getLatitude() + ", " + location.getLongitude();
        LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.addMarker(new MarkerOptions().position(newLoc).title("New Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}








