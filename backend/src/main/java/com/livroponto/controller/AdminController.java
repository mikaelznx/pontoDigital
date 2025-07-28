package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")  // Porta do seu frontend Vite/Vue
public class AdminController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegistroPontoRepository registroPontoRepository;

    @GetMapping("/funcionarios")
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    @PostMapping("/funcionarios")
    public Funcionario criarFuncionario(@RequestBody Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    @GetMapping("/pontos")
    public List<RegistroPonto> listarTodosPontos() {
        return registroPontoRepository.findAll();
    }

    @GetMapping("/pontos/{funcionarioId}")
    public List<RegistroPonto> listarPontosPorFuncionario(
            @PathVariable Long funcionarioId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId).orElse(null);
        if (funcionario == null) {
            throw new RuntimeException("Funcionário não encontrado");
        }

        return registroPontoRepository.findByFuncionarioAndDataBetween(funcionario, inicio, fim);
    }
}
