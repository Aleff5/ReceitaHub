package com.example.receitahub.data.model;

import androidx.annotation.NonNull;   // IMPORTE ADICIONADO
import androidx.annotation.Nullable; // IMPORTE ADICIONADO
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ANOTAÇÕES ADICIONADAS
    @NonNull
    public String nome;

    @NonNull
    public String email;

    @NonNull
    public String password; // Em um app real, isso deveria ser um hash!

    @Nullable // Diz ao Room que este campo pode ser nulo
    public String profilePictureUri;

    public User(@NonNull String nome, @NonNull String email, @NonNull String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        // O campo profilePictureUri agora pode ser nulo por padrão sem causar erros.
    }
}