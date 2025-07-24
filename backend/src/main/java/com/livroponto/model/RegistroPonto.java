package com.livroponto.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class RegistroPonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Funcionario funcionario;

    private LocalDate data;
    private LocalTime hora;
    private String tipo;
}
