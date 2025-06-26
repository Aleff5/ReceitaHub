package com.example.receitahub.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// ALTERADO: Adicionado um índice para a coluna 'userId' para melhorar a performance
@Entity(tableName = "receitas",
        indices = {@Index(value = "userId")},
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Receita {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public long userId;
    @NonNull
    public String titulo;
    @NonNull
    public String ingredientes;
    @NonNull
    public String modoDePreparo;
    @NonNull
    public String status;
    @Nullable
    public String mealType;

    // ADICIONADO DE VOLTA: Campo para saber se a receita é favorita
    public boolean isFavorita;
    // ADICIONADO DE VOLTA: Campo para registrar o momento da criação
    public long timestamp;

    // ALTERADO: Este construtor agora será ignorado pelo Room para evitar ambiguidades
    @Ignore
    public Receita() {}

    public Receita(long userId, @NonNull String titulo, @NonNull String ingredientes, @NonNull String modoDePreparo, @NonNull String status) {
        this.userId = userId;
        this.titulo = titulo;
        this.ingredientes = ingredientes;
        this.modoDePreparo = modoDePreparo;
        this.status = status;
        this.isFavorita = false; // Valor padrão
        this.timestamp = System.currentTimeMillis(); // Valor padrão
    }
}