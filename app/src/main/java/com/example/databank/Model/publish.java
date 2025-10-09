package com.example.databank.Model;

public class publish {

    int id;

    String publish_image, publish_title;

    public publish(int id, String publish_image, String publish_title) {
        this.id = id;
        this.publish_image = publish_image;
        this.publish_title = publish_title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublish_image() {
        return publish_image;
    }

    public void setPublish_image(String publish_image) {
        this.publish_image = publish_image;
    }

    public String getPublish_title() {
        return publish_title;
    }

    public void setPublish_title(String publish_title) {
        this.publish_title = publish_title;
    }
}
