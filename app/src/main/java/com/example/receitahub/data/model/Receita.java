package com.example.receitahub.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "receitas_favoritas")

public class Receita {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String titulo;
    public String ingredientes;
    public String modoDePreparo;
    public long timestamp;

    public Receita(){}

    public Receita(String titulo,String ingrdientes,String modoDePreparo){
        this.titulo = titulo;
        this.ingredientes = ingrdientes;
        this.modoDePreparo = modoDePreparo;
        this.timestamp = System.currentTimeMillis();
    }

}
