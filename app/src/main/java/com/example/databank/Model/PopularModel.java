package com.example.databank.Model;

import java.io.Serializable;

public class PopularModel implements Serializable {
    String name;
    String raiting;
    String rub;
    String price;
    String img_url;
    String type;
    String description;
    String phone;

    public PopularModel(String img_url) {
        this.img_url = img_url;
    }

    public PopularModel() {

    }


    public PopularModel(String phone, String name, String raiting, String rub, String price, String type, String description) {
        this.name = name;
        this.phone = phone;
        this.raiting = raiting;
        this.rub = rub;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaiting() {
        return raiting;
    }

    public void setRaiting(String raiting) {
        this.raiting = raiting;
    }

    public String getRub() {
        return rub;
    }

    public void setRub(String rub) {
        this.rub = rub;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
