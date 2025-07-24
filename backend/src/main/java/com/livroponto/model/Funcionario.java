package com.livroponto.model;

import jakarta.persistence.*;

@Entity
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String matricula;

    // Getters e setters
}
