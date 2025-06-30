package com.example.receitahub.data.model;

public class Mensagem {
    private String texto;
    private boolean enviadoPeloUsuario;
    private boolean isRecipe;
    private boolean isFavorited = false; // PASSO 1: CAMPO ADICIONADO PARA "MEMÓRIA"

    public Mensagem(String texto, boolean enviadoPeloUsuario, boolean isRecipe) {
        this.texto = texto;
        this.enviadoPeloUsuario = enviadoPeloUsuario;
        this.isRecipe = isRecipe;
    }

    public Mensagem(String texto, boolean enviadoPeloUsuario) {
        this(texto, enviadoPeloUsuario, false);
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEnviadoPeloUsuario() {
        return enviadoPeloUsuario;
    }

    public boolean isRecipe() {
        return isRecipe;
    }

    // --- MÉTODOS ADICIONADOS PARA O PASSO 1 ---
    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
    // -----------------------------------------
}