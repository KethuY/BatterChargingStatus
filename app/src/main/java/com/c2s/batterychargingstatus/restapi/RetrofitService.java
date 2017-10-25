package com.c2s.batterychargingstatus.restapi;

import com.c2s.batterychargingstatus.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by satya on 26-Sep-17.
 */

public interface RetrofitService {
    @GET("battery/getdata")
    Call<List<User>> getUserData(@Query("dateTime") String dateTime);

}
