package com.example.receitahub.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // CAMPO ADICIONADO
    public String nome;

    public String email;
    public String password; // Em um app real, isso deveria ser um hash!

    // CAMPO ADICIONADO
    public String profilePictureUri;

    /**
     * CONSTRUTOR ATUALIZADO para incluir o nome.
     * O profilePictureUri pode ser adicionado depois.
     */
    public User(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }
}