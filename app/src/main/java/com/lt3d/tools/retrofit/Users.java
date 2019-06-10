package com.lt3d.tools.retrofit;

import java.util.List;

public class Users {
    /**
     * version : 1
     * success : true
     * status : 200
     * users : [{"id":"32","pseudo":"alban"},{"id":"18","pseudo":"ChapeauMangue"},{"id":"44","pseudo":"ft"},{"id":"23","pseudo":"imen"},{"id":"35","pseudo":"inao"},{"id":"2","pseudo":"isa"},{"id":"9","pseudo":"lucia"},{"id":"11","pseudo":"lucipi"},{"id":"31","pseudo":"max"},{"id":"28","pseudo":"maximeBtn"},{"id":"13","pseudo":"nathan"},{"id":"34","pseudo":"nonin-duti"},{"id":"29","pseudo":"pierrick"},{"id":"12","pseudo":"pmr"},{"id":"40","pseudo":"romain"},{"id":"30","pseudo":"solene"},{"id":"1","pseudo":"tom"},{"id":"37","pseudo":"ulysse"},{"id":"27","pseudo":"wissem"},{"id":"26","pseudo":"wwy"}]
     */

    private int version;
    private boolean success;
    private int status;
    private List<UsersBean> users;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUserId(String pseudo) {
        for (UsersBean u: users) {
            if (u.getPseudo().equals(pseudo)){
                return u.getId();
            }
        }
        return "";
    }

    public static class UsersBean {
        /**
         * id : 32
         * pseudo : alban
         */

        private String id;
        private String pseudo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPseudo() {
            return pseudo;
        }

        public void setPseudo(String pseudo) {
            this.pseudo = pseudo;
        }
    }
}
