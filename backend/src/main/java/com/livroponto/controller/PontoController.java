package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/ponto")
@CrossOrigin(origins = "*")
public class PontoController {

    @Autowired
    private RegistroPontoRepository pontoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping
    public String registrarPonto(@RequestBody RegistroRequest request) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByMatricula(request.getMatricula());
        if (funcionarioOpt.isEmpty()) {
            return "Funcionário com matrícula não encontrado.";
        }

        Funcionario funcionario = funcionarioOpt.get();

        RegistroPonto ponto = new RegistroPonto();
        ponto.setFuncionario(funcionario);
        ponto.setNome(funcionario.getNome()); // ← ESTA LINHA PRECISA ESTAR ATIVA
        ponto.setData(LocalDate.now());
        ponto.setHora(LocalTime.now());
        ponto.setTipo("entrada");

        pontoRepository.save(ponto);

        return "Ponto registrado com sucesso para " + funcionario.getNome();
    }

    public static class RegistroRequest {
        private String matricula;

        public String getMatricula() {
            return matricula;
        }

        public void setMatricula(String matricula) {
            this.matricula = matricula;
        }
    }
}
