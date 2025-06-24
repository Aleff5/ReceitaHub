package com.example.receitahub.data.model;

public class Mensagem {

    private String texto;
    private boolean enviadaPeloUsuario;

    public Mensagem(String texto, boolean enviadaPeloUsuario) {
        this.texto = texto;
        this.enviadaPeloUsuario = enviadaPeloUsuario;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isEnviadaPeloUsuario() {
        return enviadaPeloUsuario;
    }
}