package com.lt3d.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Setting implements Serializable {
    private String url;
    private List<User> users;
    private int recentUser;

    public Setting() {
        this.url = "http://tomnab.fr/todo-api/"; //default api setting
        this.users = new ArrayList<>();
        this.recentUser = 0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public boolean isEmpty() {
        return this.users.isEmpty();
    }

    public User getRecentUser() {
        if (users.isEmpty()) {
            return null;
        }
        return this.users.get(recentUser);
    }

    public void setRecentUser(String pseudo) {
        recentUser = users.indexOf(getUser(pseudo));
    }

    public Boolean hasUser(String pseudo) {
        for (User u : users) {
            if (u.getPseudo().equals(pseudo)) return true;
        }
        return false;
    }

    public User getUser(String pseudo) {
        for (User u : users) {
            if (u.getPseudo().equals(pseudo)) return u;
        }
        return null;
    }

    public List<User> getUsers() {
        return users;
    }
}
