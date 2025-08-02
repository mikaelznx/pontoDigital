package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegistroPontoRepository registroPontoRepository;

    // Listar todos os funcionários
    @GetMapping("/funcionarios")
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    // Criar novo funcionário com validação
    @PostMapping("/funcionarios")
    public ResponseEntity<?> criarFuncionario(@Valid @RequestBody Funcionario funcionario, BindingResult result) {

        // Verificar se há erros de validação
        if (result.hasErrors()) {
            Map<String, String> erros = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    erros.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("errors", erros));
        }

        // Verificar se matrícula já existe
        if (funcionarioRepository.findByMatricula(funcionario.getMatricula()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Já existe um funcionário com esta matrícula"
            ));
        }

        try {
            Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
            return ResponseEntity.ok(funcionarioSalvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Erro ao cadastrar funcionário: " + e.getMessage()
            ));
        }
    }

    // Listar registros de ponto com filtro (funcionário e intervalo)
    @GetMapping("/pontos")
    public List<RegistroPonto> listarPontosFiltrados(
            @RequestParam(name = "funcionarioId", required = false) Long funcionarioId,
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        if (funcionarioId == null || inicio == null || fim == null) {
            return registroPontoRepository.findAll();
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        return registroPontoRepository.findByFuncionarioAndDataBetween(funcionario, inicio, fim);
    }

    // PUT para atualizar horário e data do ponto por ID
    @PutMapping("/pontos/{id}")
    public ResponseEntity<?> atualizarPonto(
            @PathVariable Long id,
            @RequestBody AtualizaPontoRequest body) {

        return registroPontoRepository.findById(id)
                .map(ponto -> {
                    try {
                        if (body.getHora() != null && !body.getHora().trim().isEmpty()) {
                            ponto.setHora(LocalTime.parse(body.getHora()));
                        }
                        if (body.getData() != null && !body.getData().trim().isEmpty()) {
                            ponto.setData(LocalDate.parse(body.getData()));
                        }
                        registroPontoRepository.save(ponto);
                        return ResponseEntity.ok(ponto);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "message", "Formato inválido. Use HH:mm para hora e YYYY-MM-DD para data"
                        ));
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Classe para receber dados de atualização de ponto
    public static class AtualizaPontoRequest {
        private String hora;
        private String data;

        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }

    // Manter compatibilidade com código antigo
    public static class AtualizaHoraRequest {
        private String hora;

        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
    }
}