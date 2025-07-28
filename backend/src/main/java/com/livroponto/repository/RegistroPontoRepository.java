package com.livroponto.repository;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroPontoRepository extends JpaRepository<RegistroPonto, Long> {

    // ✅ Para AdminController
    List<RegistroPonto> findByFuncionarioAndDataBetween(
            Funcionario funcionario,
            LocalDate dataInicio,
            LocalDate dataFim
    );

    // ✅ Para RelatorioController e RelatorioService
    List<RegistroPonto> findByFuncionarioIdAndDataBetweenOrderByDataAscHoraAsc(
            Long funcionarioId,
            LocalDate dataInicio,
            LocalDate dataFim
    );
}