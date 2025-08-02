package com.livroponto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import java.math.BigDecimal;

@Entity
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Matrícula é obrigatória")
    @Column(unique = true)
    private String matricula;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Carga horária é obrigatória")
    @DecimalMin(value = "1.0", message = "Carga horária deve ser pelo menos 1 hora")
    @DecimalMax(value = "12.0", message = "Carga horária não pode exceder 12 horas")
    @Column(name = "carga_horaria", precision = 3, scale = 1)
    private BigDecimal cargaHoraria;

    // Getters e Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getCargaHoraria() {
        return cargaHoraria;
    }
    public void setCargaHoraria(BigDecimal cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }
}