package com.lt3d.tools.retrofit;

import java.util.List;

public class Books {

    /**
     * version : 1
     * success : true
     * status : 200
     * lists : [{"id":"35","label":"test2"},{"id":"36","label":"test1"}]
     */

    private int version;
    private boolean success;
    private int status;
    private List<ListsBean> lists;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public static class ListsBean {
        /**
         * id : 35
         * label : test2
         */

        private String id;
        private String label;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
