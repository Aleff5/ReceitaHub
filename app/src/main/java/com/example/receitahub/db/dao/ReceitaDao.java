package com.example.receitahub.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.receitahub.data.model.Receita;
import java.util.List;

@Dao
public interface ReceitaDao {

    @Insert
    void salvarReceita(Receita receita);

    @Query("SELECT * FROM receitas_favoritas ORDER BY timestamp DESC")
    LiveData<List<Receita>> getTodasReceitasFavoritas();

    @Query("SELECT * FROM receitas_favoritas WHERE id = :receitaId")
    Receita getReceitaPorId(int receitaId);

    @Query("DELETE FROM receitas_favoritas WHERE id = :receitaId")
    void deletarReceita(int receitaId);
}

