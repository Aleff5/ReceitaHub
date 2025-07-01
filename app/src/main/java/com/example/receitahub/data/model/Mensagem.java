package com.example.receitahub.data.model;

public class Mensagem {
    private String texto;
    private boolean enviadoPeloUsuario;
    private boolean isRecipe;
    private boolean isFavorited = false;

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

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}