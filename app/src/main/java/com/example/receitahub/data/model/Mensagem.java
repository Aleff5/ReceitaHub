package com.example.receitahub.data.model;

public class Mensagem {
    private String texto;
    private boolean enviadoPeloUsuario;

    public Mensagem(String texto, boolean enviadoPeloUsuario) {
        this.texto = texto;
        this.enviadoPeloUsuario = enviadoPeloUsuario;
    }

    public String getTexto() {
        return texto;
    }

    // ESTE É O MÉTODO QUE ESTÁ FALTANDO NO SEU ARQUIVO
    public boolean isEnviadoPeloUsuario() {
        return enviadoPeloUsuario;
    }
}