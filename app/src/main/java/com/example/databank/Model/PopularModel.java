package com.example.databank.Model;

import java.io.Serializable;

public class PopularModel implements Serializable {
    private String name;
    private String img_url;
    private String description;
    private String age;
    private String data;
    private String place;
    private String url;
    private String cash;

    public PopularModel() {
    }

    public PopularModel(String name, String img_url, String description, String age, String data, String place, String url, String cash) {
        this.name = name;
        this.img_url = img_url;
        this.description = description;
        this.age = age;
        this.data = data;
        this.place = place;
        this.url = url;
        this.cash = cash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }
}
