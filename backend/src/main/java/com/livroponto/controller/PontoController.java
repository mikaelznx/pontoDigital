package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PontoController {
    private final FuncionarioRepository funcionarioRepo;
    private final RegistroPontoRepository pontoRepo;

    public PontoController(FuncionarioRepository funcionarioRepo, RegistroPontoRepository pontoRepo) {
        this.funcionarioRepo = funcionarioRepo;
        this.pontoRepo = pontoRepo;
    }

    @PostMapping("/registrar-ponto")
    public String registrar(@RequestBody String matricula) {
        Optional<Funcionario> opt = funcionarioRepo.findByMatricula(matricula.trim());
        if (opt.isEmpty()) return "Funcionário não encontrado";

        RegistroPonto ponto = new RegistroPonto();
        ponto.setFuncionario(opt.get());
        ponto.setData(LocalDate.now());
        ponto.setHora(LocalTime.now());
        ponto.setTipo("ENTRADA"); // Simples: sempre entrada no MVP

        pontoRepo.save(ponto);
        return "Ponto registrado com sucesso";
    }
}
