package com.lt3d.tools.retrofit;

import java.util.List;

public class Models {
    /**
     * version : 1
     * success : true
     * status : 200
     * items : [{"id":"37","label":"test1","url":null,"checked":"0"},{"id":"38","label":"test2","url":null,"checked":"0"}]
     */

    private int version;
    private boolean success;
    private int status;
    private List<ItemsBean> items;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * id : 37
         * label : test1
         * url : null
         * checked : 0
         */

        private String id;
        private String label;
        private Object url;
        private String checked;

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

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

        public String getChecked() {
            return checked;
        }

        public void setChecked(String checked) {
            this.checked = checked;
        }
    }
}
