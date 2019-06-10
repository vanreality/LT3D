package com.lt3d.tools.retrofit;

import com.google.gson.annotations.SerializedName;

public class HashData {
    @SerializedName("version")
    public int version;

    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;

    @SerializedName("hash")
    public String hash;

    public String getHash() {
        return this.hash;
    }
}
