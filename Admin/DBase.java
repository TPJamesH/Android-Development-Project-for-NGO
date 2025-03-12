package com.example.assessment2;

import static android.content.ContentValues.TAG;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class DBase {
    private final DatabaseReference mDatabase;

    private final DatabaseReference personBase;

    private final DatabaseReference siteBase;

    private final DatabaseReference eventBase;

    private final DatabaseReference adminBase;

    private int finalID_person;

    private String final_event;

    private int finalID_site;

    private final List<Person> personList;

    private final List<Event> eventList;


    private int id;

    public DBase() {
        String url = "https://assessment2-9b283-default-rtdb.asia-southeast1.firebasedatabase.app/";
        mDatabase = FirebaseDatabase.getInstance(url).getReference();
        personBase = mDatabase.child("Person");
        eventBase = mDatabase.child("Event");
        siteBase = eventBase.child("Site");
        personList = getPersonList();
        eventList = getEventList();

        adminBase = mDatabase.child("Admin");




    }



    public void writeNewEvent(String description, String date, int goal_num, double lat, double lon, String username) {
        if (description.isEmpty() ||
                date.isEmpty() ||
                String.valueOf(goal_num).isEmpty() ||
                String.valueOf(lat).isEmpty() ||
                String.valueOf(lon).isEmpty()) {
            return;
        }
        LocationOwner temp = new LocationOwner();

        for(int i = 0 ; i < personList.size(); i++){
            if(username.equals(personList.get(i).getName())){
                temp.setID(personList.get(i).getID());
                temp.setName(Objects.requireNonNull(personList.get(i).getName()));
                temp.setPassword(Objects.requireNonNull(personList.get(i).getPassword()));
                temp.setPhoneNumber(Objects.requireNonNull(personList.get(i).getPhoneNumber()));
            }
        }

        Site site = new Site(final_event,lat,lon,description,date,goal_num );

        eventBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventBase.
                        child(String.valueOf(Integer.parseInt(final_event) + 1))
                        .child("LocationOwner").
                        setValue(temp);

                eventBase.
                        child(String.valueOf(Integer.parseInt(final_event) + 1))
                        .child("Site").
                        setValue(site);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.

            }
        });
    }

    public void modifyEvent(String description, String date, int goal_num, double lat, double lon, String username, String id) {
        if (description.isEmpty() || date.isEmpty() ||
                String.valueOf(goal_num).isEmpty() ||
                String.valueOf(lat).isEmpty() ||
                String.valueOf(lon).isEmpty()) {
            return;
        }
        LocationOwner temp = new LocationOwner();

        for(int i = 0 ; i < personList.size(); i++){
            if(username.equals(personList.get(i).getName())){
                temp.setID(personList.get(i).getID());
                temp.setName(Objects.requireNonNull(personList.get(i).getName()));
                temp.setPassword(Objects.requireNonNull(personList.get(i).getPassword()));
                temp.setPhoneNumber(Objects.requireNonNull(personList.get(i).getPhoneNumber()));
            }
        }

        Site site = new Site(final_event,lat,lon,description,date,goal_num );

        eventBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventBase.
                        child(id)
                        .child("LocationOwner").
                        setValue(temp);

                eventBase.
                        child(id)
                        .child("Site").
                        setValue(site);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.

            }
        });
    }
    /////////////////////////////////////////

    ///////////////////////////////////////
    public void writeNewUser(String name, String password, String phone) {
        if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            return;
        }

        Person person = new Person(String.valueOf(finalID_person + 1), name, password, phone);
        personBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                personBase.
                        child(String.valueOf(finalID_person + 1)).
                        setValue(person);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.

            }
        });
    }


    public List<Person> getPersonList() {
        List<Person> temp_personList = new ArrayList<>();
        personBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Person temp = new Person();
                temp.setID(Objects.requireNonNull(dataSnapshot.getKey()));
                finalID_person = Integer.parseInt(temp.getID());
                temp.setName(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getName());

                temp.setPassword(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getPassword());
                temp.setPhoneNumber(Objects.requireNonNull(dataSnapshot.getValue(Person.class)).getPhoneNumber());

                temp_personList.add(temp);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        return temp_personList;
    }


    public List<Admin> getAdminList() {
        List<Admin> temp_adminList = new ArrayList<>();
        adminBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Admin temp = new Admin();
                temp.setID(Objects.requireNonNull(dataSnapshot.getKey()));
                finalID_person = Integer.parseInt(temp.getID());
                temp.setName(Objects.requireNonNull(dataSnapshot.getValue(Admin.class)).getName());

                temp.setPassword(Objects.requireNonNull(dataSnapshot.getValue(Admin.class)).getPassword());
                temp.setPhoneNumber(Objects.requireNonNull(dataSnapshot.getValue(Admin.class)).getPhoneNumber());

                temp_adminList.add(temp);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
        return temp_adminList;
    }


    public DatabaseReference getEventBase() {
        return eventBase;
    }

    public ArrayList<Event> getEventList() {
        final int[] i = {0};
        ArrayList<Event> temp_eventList = new ArrayList<>();

        eventBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Event temp = new Event();
                Site temp_site = new Site();
                Volunteer volunteer = new Volunteer();
                LocationOwner temp_owner = new LocationOwner();
                final_event = String.valueOf(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())));
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                        if(i[0] == 0) {
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
                        if(i[0] == 1) {
                            temp_site.setDescription(Objects.
                                    requireNonNull(snapShot.getValue(Site.class)).getDescription());

                            temp_site.setId(Objects.
                                    requireNonNull(snapShot.getValue(Site.class)).getId());

                            temp_site.setLat(Objects.
                                    requireNonNull(snapShot.getValue(Site.class)).getLat());

                            temp_site.setLon(Objects.
                                    requireNonNull(snapShot.getValue(Site.class)).getLon());
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

                /////////////////////////////////////////////
                temp.setVolunteer(volunteer);
                temp.setLocation(temp_site);
                temp.setOwner(temp_owner);


                temp_eventList.add(temp);
                final_event = String.valueOf(Integer.parseInt(Objects.requireNonNull(dataSnapshot.getKey())));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                temp_eventList.remove(snapshot.getValue(Event.class));

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

        return temp_eventList;
    }


    }





