package com.c2s.batterychargingstatus.restapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by satya on 26-Sep-17.
 */

public class RetrofitClient {
   // private static final String SERVER_URL="http://samples.openweathermap.org/data/2.5/forecast";
    private static final String SERVER_URL="http://batterychargeservice.azurewebsites.net/api/";//Must end with /
    private static  Retrofit mRetrofit =null;

    public static Retrofit getInstance(){

        if(mRetrofit==null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

       return mRetrofit;
    }

}
