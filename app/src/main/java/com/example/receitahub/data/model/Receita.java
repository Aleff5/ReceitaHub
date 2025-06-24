package com.example.receitahub.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "receitas",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE))
public class Receita {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long userId;
    public String titulo;
    public String ingredientes;
    public String modoDePreparo;
    public long timestamp;
    public String tipo;
    public boolean isFavorita;

    public Receita() {}

    public Receita(long userId, String titulo, String ingredientes, String modoDePreparo, String tipo) {
        this.userId = userId;
        this.titulo = titulo;
        this.ingredientes = ingredientes;
        this.modoDePreparo = modoDePreparo;
        this.tipo = tipo;
        this.isFavorita = false;
        this.timestamp = System.currentTimeMillis();
    }
}