package com.elinium.util.demo.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by amiri on 9/10/2017.
 */

@Entity
public class User {
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    public int getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    // Getters and setters are ignored for brevity,
    // but they're required for Room to work.
}