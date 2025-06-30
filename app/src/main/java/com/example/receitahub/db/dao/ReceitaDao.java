package com.example.receitahub.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete; // Import necessário para a exclusão por objeto
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.receitahub.data.model.Receita;
import java.util.List;

@Dao
public interface ReceitaDao {

    // OK: Método para inserir (seu 'salvarReceita' já funciona como insert)
    @Insert
    void salvarReceita(Receita receita);

    // OK: Método para atualizar. Renomeei para 'update' para seguir o padrão.
    // O seu 'updateReceita' também funcionaria.
    @Update
    void update(Receita receita);

    // OK: Método para buscar todas as receitas.
    @Query("SELECT * FROM receitas ORDER BY timestamp DESC")
    LiveData<List<Receita>> getTodasReceitas();

    // OK: Método para buscar uma receita pelo ID. Ajustei para receber 'long'.
    @Query("SELECT * FROM receitas WHERE id = :receitaId")
    Receita getReceitaById(long receitaId);

    // MUDANÇA PRINCIPAL: Adicionado um método @Delete.
    // É mais eficiente e seguro passar o objeto inteiro para o Room deletar,
    // em vez de deletar apenas pelo ID. O código do seu Fragment já usa essa abordagem.
    @Delete
    void delete(Receita receita);

    // OK: Método para buscar por status.
    @Query("SELECT * FROM receitas WHERE status = :status ORDER BY id DESC")
    LiveData<List<Receita>> findByStatus(String status);

    // OK: Método para buscar as favoritas.
    @Query("SELECT * FROM receitas WHERE isFavorita = 1 ORDER BY timestamp DESC")
    LiveData<List<Receita>> getFavoritas();

    /*
     O método abaixo não é mais necessário, pois foi substituído pelo @Delete
     que é mais robusto.
     @Query("DELETE FROM receitas WHERE id = :receitaId")
     void deletarReceita(int receitaId);
    */
}