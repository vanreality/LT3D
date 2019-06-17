package com.lt3d.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Models {
    public List<Model> models;

    public Models() {
    }

    public Models(List<Model> models) {
        this.models = models;
    }
}
