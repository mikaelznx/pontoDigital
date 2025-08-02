package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    // Criar novo funcionário
    @PostMapping("/funcionarios")
    public Funcionario criarFuncionario(@RequestBody Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
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

    // PUT para atualizar horário do ponto por ID
    @PutMapping("/pontos/{id}")
    public ResponseEntity<?> atualizarHorarioPonto(
            @PathVariable Long id,
            @RequestBody AtualizaHoraRequest body) {

        return registroPontoRepository.findById(id)
                .map(ponto -> {
                    try {
                        ponto.setHora(LocalTime.parse(body.getHora()));
                        registroPontoRepository.save(ponto);
                        return ResponseEntity.ok(ponto);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body("Formato de hora inválido. Use HH:mm");
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class AtualizaHoraRequest {
        private String hora;

        public String getHora() { return hora; }
        public void setHora(String hora) { this.hora = hora; }
    }
}
