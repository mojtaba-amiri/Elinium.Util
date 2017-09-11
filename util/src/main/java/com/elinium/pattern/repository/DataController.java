package com.elinium.pattern.repository;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.elinium.pattern.repository.room.SampleDatabase;
import com.elinium.pattern.repository.room.User;
import com.elinium.pattern.repository.room.UserDao;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by amiri on 9/10/2017.
 */

public class DataController<DB,WEB_API> {
    Retrofit retrofit;
    SampleDatabase db;

    private void initLocalDB(Context appContext){
        db = Room.databaseBuilder(appContext, SampleDatabase.class, "db").build();
    }

    private void initWeb(String serverBaseUrl, Class<? extends Object> webCallsClass) {
        retrofit = new Retrofit.Builder().baseUrl(serverBaseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(webCallsClass);
    }

    private void loadUsers(){
        db.userDao().
    }

}
