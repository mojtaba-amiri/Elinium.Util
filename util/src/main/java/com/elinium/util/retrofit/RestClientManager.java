package com.elinium.util.retrofit;

import android.content.Context;
import android.util.Log;

import com.elinium.util.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amiri on 9/16/2017.
 */

public class RestClientManager {
    private static final String TAG = "NexRestClient";
    // private static NexApiService nexApiService;
    private static List<Object> restClients = new ArrayList<>();
    private Context context;
    private static RestClientManager instance = null;


    public static void setup(Context context) {
        new RestClientManager(context);
    }

    private RestClientManager(Context context) {
        if (instance == null) instance = new RestClientManager(context);
        ManagerFor annotation = context.getClass().getAnnotation(ManagerFor.class);
        AddInterceptor interceptor = context.getClass().getAnnotation(AddInterceptor.class);

        if (annotation != null && annotation.endpoints().length > 0) {
            for (Class<? extends Object> endpoint : annotation.endpoints()) {
                Endpoint endpointAnnot = endpoint.getAnnotation(Endpoint.class);
                if (endpointAnnot != null) {
                    setupRestClient(endpoint, endpointAnnot.baseUrl(), interceptor != null ? interceptor.interceptors() : null);
                }
            }
        }
    }


    public <T> void setupRestClient(Class<T> endpointClass, String endpointBaseUrl, int timeoutSeconds, Class<? extends Interceptor>[] interceptors) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Gson gson = gsonBuilder.create();

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(timeoutSeconds, TimeUnit.SECONDS);
        builder.writeTimeout(timeoutSeconds, TimeUnit.SECONDS);
        builder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS);

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

        Retrofit restAdapter = new Retrofit.Builder().baseUrl(endpointBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        for (Object obj : restClients) {
            if (endpointClass.isInstance(obj)) {
                restClients.remove(obj);
            }
        }
        restClients.add(restAdapter.create(endpointClass));
    }

    public <T> T getEndpoint(Class<T> tClass) {
        try {
            for (Object obj : restClients) if (tClass.isInstance(obj)) return (T) obj;
        } catch (Exception e) {
            Log.e(TAG, "getClient error:" + e.getMessage());
        }
        return null;
    }

    protected <T> Retrofit.Builder getBuilder(Class<T> endpointClass) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
//        builder.readTimeout(55, TimeUnit.SECONDS);
//        builder.writeTimeout(55, TimeUnit.SECONDS);
//        builder.connectTimeout(55, TimeUnit.SECONDS);

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

        return null;
    }

}
