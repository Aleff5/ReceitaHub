package com.example.receitahub.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update; // IMPORT ADICIONADO

import com.example.receitahub.data.model.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User getUserByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(long userId);

    // MÉTODO ADICIONADO para atualizar os dados de um usuário
    @Update
    void updateUser(User user);
}