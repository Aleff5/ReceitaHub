package com.example.receitahub.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String email;
    public String password; // Em um app real, isso deveria ser um hash!

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}