package com.lt3d.data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Books {
    public List<Book> books;

    public Books() {
    }

    public Books(List<Book> books) {
        this.books = books;
    }
}
