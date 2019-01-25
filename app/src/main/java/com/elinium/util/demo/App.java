package com.elinium.util.demo;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.elinium.util.Async;
import com.elinium.util.demo.repository.local.database.SampleDatabase;

/**
 * Created by amiri on 9/16/2017.
 */

public class App extends Application {
    private static final String TAG = "DemoApp";

    static SampleDatabase db;


    @Override
    public void onCreate() {
        super.onCreate();
        Async.Do(() -> {
            db = Room.databaseBuilder(getApplicationContext(), SampleDatabase.class, "demo-db").build();
//            List<User> userList = db.userDao().getAll();
//            Log.d(TAG, "User Size:" + userList.size());
//            db.userDao().insertAll(new User("sample", "last Sample"));
        });
    }
}
