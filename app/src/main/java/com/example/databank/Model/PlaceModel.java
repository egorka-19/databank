package com.example.databank.Model;

import java.io.Serializable;

public class PlaceModel implements Serializable {
    private String name;
    private String description;
    private int imageResourceId;
    private String type;
    private String age;
    private String data;
    private String place;


    public PlaceModel(String name, String description, int imageResourceId, String type, String age, String data, String place) {
        this.name = name;
        this.age = age;
        this.place = place;
        this.data = data;

        this.description = description;
        this.imageResourceId = imageResourceId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getData() {
        return data;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getType() {
        return type;
    }
}
