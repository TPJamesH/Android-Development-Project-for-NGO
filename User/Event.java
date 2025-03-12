package com.example.assessment2;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Event{

    private Site location;

    private LocationOwner owner;



    private Volunteer[] volunteers;

    public Event() {
        location = new Site();
        owner = new LocationOwner();

      this.volunteers = new Volunteer[]{};
    }

    public Event(Site location, LocationOwner owner) {
        this.location = location;
        this.owner = owner;
        this.volunteers = new Volunteer[]{};
    }



    public Volunteer[] getVolunteers() {
        return volunteers;
    }

    public void setVolunteer(Volunteer volunteer){
        addVolunteer(this.volunteers, volunteer);
    }

    public void setVolunteers(Volunteer[] volunteers) {
        this.volunteers = volunteers;
    }

    public Site getLocation() {
        return location;
    }

    public LocationOwner getOwner() {
        return owner;
    }

    public void setLocation(@NonNull Site location) {
        this.location.setId(location.getId());
        this.location.setDescription(location.getDescription());
        this.location.setLat(location.getLat());
        this.location.setLon(location.getLon());
        this.location.setGoal(location.getGoal());
        this.location.setDate(location.getDate());
    }

    public void setOwner(@NonNull LocationOwner owner) {
        this.owner.setID(owner.getID());
        this.owner.setName(owner.getName());
        this.owner.setPassword(owner.getPassword());
        this.owner.setPhoneNumber(owner.getPhoneNumber());
    }

    public void addVolunteer(Volunteer[] arr, Volunteer x)
    {
        int i;

        // create a new ArrayList
        List<Volunteer> arrlist
                = new ArrayList<>(
                Arrays.asList(arr));

        // Add the new element
        arrlist.add(x);

        // Convert the Arraylist to array
        this.volunteers = arrlist.toArray(arr);

    }

    public List<Volunteer> getVolunteer()
    {

        // create a new ArrayList

        return new ArrayList<>(
        Arrays.asList(this.volunteers));

    }

}
