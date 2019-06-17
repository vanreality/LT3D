package com.lt3d.data;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    public String title;
    public String type;

    public Book() {
    }

    public Book(String title, String type) {
        this.title = title;
        this.type = type;
    }
}
