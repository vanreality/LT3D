package com.lt3d.data;

public class Model {
    //TODO use this class to store model data in the firebase
    public String data = "data";
    public String title;

    public Model() {
    }

    public Model(String title, String data) {
        this.title = title;
        this.data = data;
    }
}
