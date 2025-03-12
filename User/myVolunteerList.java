package com.example.assessment2;

import android.os.Bundle;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assessment2.databinding.ActivityMySiteBinding;
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
import com.example.assessment2.databinding.ActivityMyVolunteerListBinding;

public class myVolunteerList extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static CustomAdapter_Event_I_Volunteered adapter;

    private final DBase database = new DBase();
    private ListView eventsLV;



    private ArrayList<Event> eventsList = new ArrayList<>();


    private final ArrayList<Event> events_marker = database.getEventList();
    private Volunteer[] volunteerList = new Volunteer[events_marker.size()];
    private String username;

    private ActivityMyVolunteerListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler ha = new Handler(); //Handler for the method calling
        int delay = 500; //0.5 secs
        binding = ActivityMyVolunteerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Button return_to_menu = findViewById(R.id.return_to_menu);

        try {
            readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        eventsLV = findViewById(R.id.events_volunteered);

        adapter = new CustomAdapter_Event_I_Volunteered(eventsList, getApplicationContext());

        ////////////////////////////////////////
        eventsLV.setAdapter(adapter);
        initializeView();


        ha.postDelayed(() -> {
            if(eventsList.size() == 0){
                setVisible(R.id.events_volunteered,false);
                setVisible(R.id.textView5,true);
            }
            else{
                setVisible(R.id.events_volunteered, true);
                setVisible(R.id.textView5,false);
            }
        }, 1000);




        return_to_menu.setOnClickListener(v->{
            Intent intent = new Intent(this, User_Choice.class);
            intent.putExtra("message", "Welcome "+ username);
            startActivity(intent);

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
                    if(i[0] == 2) {
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
                //if current user is volunteering in the event and not already added in the list
                if ((volunteer.getName().equals(username) & b)) {
                    temp.setLocation(temp_site);
                    temp.setOwner(temp_owner);
                    temp.addVolunteer(volunteerList,volunteer);

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


}