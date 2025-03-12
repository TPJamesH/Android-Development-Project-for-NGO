package com.example.assessment2;

public class LocationOwner extends Person {

    private boolean isOwner;

    public LocationOwner() {
        super();
        this.isOwner = true;
    }

    public LocationOwner(String ID, String name, String pass, String phone) {
        super(ID, name, pass,phone);
        isOwner = true;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
