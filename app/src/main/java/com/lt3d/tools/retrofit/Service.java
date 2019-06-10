package com.lt3d.tools.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Service {
    @FormUrlEncoded
    @POST("authenticate")
    Call<HashData> authenticate(@Field("user") String user,
                                @Field("password") String password);
}
