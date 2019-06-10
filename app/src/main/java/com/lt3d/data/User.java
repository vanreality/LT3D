package com.lt3d.data;

import java.io.Serializable;

public class User implements Serializable {
    private String pseudo;
    private String password;
    private String hash;

    public User(String pseudo, String password) {
        this.pseudo = pseudo;
        this.password = password;
        this.hash = "";
    }

    public User(String pseudo, String password, String hash) {
        this.pseudo = pseudo;
        this.password = password;
        this.hash = hash;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean verify(String password) {
        if (this.password.equals(password)) return true;
        return false;
    }
}
