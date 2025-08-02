package com.livroponto.service;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
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

    public JasperPrint gerarRelatorioMensal(Long funcionarioId, int mes, int ano) throws Exception {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = YearMonth.of(ano, mes).atEndOfMonth();

        List<RegistroPonto> registros = pontoRepository
                .findByFuncionarioIdAndDataBetweenOrderByDataAscHoraAsc(funcionarioId, inicio, fim);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nomeFuncionario", funcionario.getNome());
        parametros.put("matricula", funcionario.getMatricula());
        parametros.put("mesAno", formatarMes(mes) + "/" + ano);

        InputStream reportStream = new ClassPathResource("relatorios/relatorio_ponto.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(registros);

        return JasperFillManager.fillReport(jasperReport, parametros, dataSource);
    }

    private String formatarMes(int mes) {
        String[] nomes = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return nomes[mes - 1];
    }
}
