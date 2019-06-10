package com.lt3d.tools.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    @FormUrlEncoded
    @POST("authenticate")
    Call<HashData> authenticate(@Field("user") String user,
                                @Field("password") String password);

    @GET("users")
    Call<Users> getUsers(@Query("hash") String hash);

    @POST("users")
    Call<ResponseBody> signup(@Header("hash") String hash,
                              @Query("pseudo") String pseudo,
                              @Query("pass") String password);

    @GET("lists")
    Call<Books> getBooks(@Query("hash") String hash);

    @GET("lists/{id}/items")
    Call<Models> getModels(@Header("hash") String hash,
                         @Path("id") String id);


}
