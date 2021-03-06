package com.elinium.util.demo.repository.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.elinium.util.demo.model.User;
import com.elinium.util.demo.repository.local.dao.UserDao;

/**
 * Created by amiri on 9/10/2017.
 * After creating the files above, you get an instance of the created database using the following code:
     AppDatabase db = Room.databaseBuilder(getApplicationContext(),  AppDatabase.class, "database-name").build();

 */

@Database(entities = {User.class}, version = 1)
public abstract class SampleDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}