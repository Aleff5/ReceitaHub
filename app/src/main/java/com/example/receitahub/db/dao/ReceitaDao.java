package com.example.receitahub.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.receitahub.data.model.Receita;
import java.util.List;

@Dao
public interface ReceitaDao {

    @Insert
    void salvarReceita(Receita receita);

    @Update
    void update(Receita receita);

    @Query("SELECT * FROM receitas ORDER BY timestamp DESC")
    LiveData<List<Receita>> getTodasReceitas();

    @Query("SELECT * FROM receitas WHERE id = :receitaId")
    Receita getReceitaById(long receitaId);

    @Delete
    void delete(Receita receita);

    @Query("SELECT * FROM receitas WHERE status = :status ORDER BY id DESC")
    LiveData<List<Receita>> findByStatus(String status);

    @Query("SELECT * FROM receitas WHERE isFavorita = 1 ORDER BY timestamp DESC")
    LiveData<List<Receita>> getFavoritas();
}