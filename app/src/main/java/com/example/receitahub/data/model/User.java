package com.example.receitahub.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String nome;

    @NonNull
    public String email;

    @NonNull
    public String password;

    @Nullable
    public String profilePictureUri;

    public User(@NonNull String nome, @NonNull String email, @NonNull String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }
}