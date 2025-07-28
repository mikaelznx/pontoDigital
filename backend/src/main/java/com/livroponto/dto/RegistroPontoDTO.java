package com.livroponto.dto;

import com.livroponto.model.RegistroPonto;

public class RegistroPontoDTO {
    private Long id;
    private String nome;
    private String data;
    private String hora;
    private String tipo;

    public RegistroPontoDTO(RegistroPonto registro) {
        this.id = registro.getId();
        this.nome = registro.getFuncionario().getNome();
        this.data = registro.getData().toString();
        this.hora = registro.getHora().toString();
        this.tipo = registro.getTipo(); // ✅ corrigido aqui
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getTipo() {
        return tipo;
    }
}
