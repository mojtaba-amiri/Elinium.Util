package com.elinium.util.demo.repository.web.restclient;

/**
 * Created by amiri on 9/16/2017.
 */


import com.elinium.util.retrofit.ManagerFor;
import com.elinium.util.demo.model.User;
import com.elinium.util.demo.repository.web.endpoints.Accounts;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mightycast.band.model.MCBandSession;
import com.mightycast.band.model.MCBandUser;
import com.mightycast.band.util.GsonUTCDateAdapter;
import com.mightycast.band.util.MCBandLogger;
import com.mightycast.nex.BuildConfig;
import com.mightycast.nex.model.FeedPost;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@ManagerFor(endpoints = {User.class, Accounts.class})
public class RestClient {
    private static final String TAG = "NexRestClient";
    // private static NexApiService nexApiService;

    private RestClient() {
    }

    static {
        setupRestClient();
    }

    public static void setupRestClient() {
        setupRestClient();
    }

    public static <T> void setupRestClient(Class<T> endpointClass, String endpointBaseUrl) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(55, TimeUnit.SECONDS);
        builder.writeTimeout(55, TimeUnit.SECONDS);
        builder.connectTimeout(55, TimeUnit.SECONDS);

//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(loggingInterceptor);
//            builder.addNetworkInterceptor(new StethoInterceptor());
//        }

//        builder.addInterceptor(
//                new NexApiHeaderInterceptor(myAuthToken, MCBandSession.getDeviceId(), longi, lat));

        //builder.addInterceptor(new UnauthorisedInterceptor());

        OkHttpClient client = builder.build();

        Retrofit restAdapter = new Retrofit.Builder().baseUrl(BuildConfig.endpoint_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        nexApiService = restAdapter.create(NexApiService.class);
    }

    public static NexApiService getNexApiService() {
        return nexApiService;
    }
}