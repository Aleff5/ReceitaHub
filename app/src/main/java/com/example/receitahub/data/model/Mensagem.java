package com.example.receitahub.data.model;

public class Mensagem {
    private String texto;
    private boolean enviadoPeloUsuario;
    private boolean isRecipe; // CAMPO ADICIONADO

    /**
     * Construtor principal que define se a mensagem é uma receita.
     */
    public Mensagem(String texto, boolean enviadoPeloUsuario, boolean isRecipe) {
        this.texto = texto;
        this.enviadoPeloUsuario = enviadoPeloUsuario;
        this.isRecipe = isRecipe;
    }

    /**
     * Construtor antigo para mensagens que não são receitas (como as do usuário).
     * Por padrão, define isRecipe como false.
     */
    public Mensagem(String texto, boolean enviadoPeloUsuario) {
        this(texto, enviadoPeloUsuario, false);
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEnviadoPeloUsuario() {
        return enviadoPeloUsuario;
    }

    /**
     * MÉTODO ADICIONADO para verificar se esta mensagem é uma receita.
     * @return true se for uma receita, false caso contrário.
     */
    public boolean isRecipe() {
        return isRecipe;
    }
}