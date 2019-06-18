package com.lt3d.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    public List<String> library;

    public User() {
    }

    public User(List<String> library) {
        this.library = library;
    }
}
