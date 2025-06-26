package com.example.receitahub.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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
    void updateReceita(Receita receita); // Adicionei um método de update para consistência

    // ALTERADO: Método renomeado para maior clareza
    @Query("SELECT * FROM receitas ORDER BY timestamp DESC")
    LiveData<List<Receita>> getTodasReceitas();

    @Query("SELECT * FROM receitas WHERE id = :receitaId")
    Receita getReceitaPorId(int receitaId);

    @Query("DELETE FROM receitas WHERE id = :receitaId")
    void deletarReceita(int receitaId);

    @Query("SELECT * FROM receitas WHERE status = :status ORDER BY id DESC")
    LiveData<List<Receita>> findByStatus(String status);

    @Query("SELECT * FROM receitas WHERE isFavorita = 1 ORDER BY timestamp DESC")
    LiveData<List<Receita>> getFavoritas();
}