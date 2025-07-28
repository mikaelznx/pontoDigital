package com.livroponto.service;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private final RegistroPontoRepository pontoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public RelatorioService(RegistroPontoRepository pontoRepository, FuncionarioRepository funcionarioRepository) {
        this.pontoRepository = pontoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public JasperPrint gerarRelatorioMensal(Long funcionarioId, int mes, int ano) throws Exception {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = YearMonth.of(ano, mes).atEndOfMonth();

        List<RegistroPonto> registros = pontoRepository.findByFuncionarioIdAndDataBetweenOrderByDataAscHoraAsc(funcionarioId, inicio, fim);

        Map<String, Object> params = new HashMap<>();
        params.put("NOME_FUNCIONARIO", funcionario.getNome());
        params.put("MES", inicio.getMonth().toString() + " " + ano);

        InputStream jrxml = new ClassPathResource("relatorios/relatorio_ponto.jrxml").getInputStream();
        JasperReport report = JasperCompileManager.compileReport(jrxml);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(registros);

        return JasperFillManager.fillReport(report, params, dataSource);
    }
}
