package com.livroponto.controller;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/admin/relatorios")
public class RelatorioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegistroPontoRepository registroPontoRepository;

    @GetMapping("/ponto")
    public void gerarRelatorioPonto(
            @RequestParam Long funcionarioId,
            @RequestParam String mes,
            HttpServletResponse response
    ) {
        try {
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId);
            if (funcionarioOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Funcionário não encontrado.");
                return;
            }

            Funcionario funcionario = funcionarioOpt.get();

            // Converte "2025-07" para range de datas do mês
            YearMonth yearMonth = YearMonth.parse(mes); // formato YYYY-MM
            LocalDate inicio = yearMonth.atDay(1);
            LocalDate fim = yearMonth.atEndOfMonth();

            List<RegistroPonto> registros = registroPontoRepository.findByFuncionarioIdAndDataBetweenOrderByDataAscHoraAsc(
                    funcionarioId, inicio, fim
            );

            if (registros.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT, "Nenhum registro encontrado.");
                return;
            }

            // Carrega o template .jrxml
            InputStream jasperStream = getClass().getResourceAsStream("/templates/relatorio_ponto.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            // DataSource com os dados dos pontos
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(registros);

            // Parâmetros para o relatório
            Map<String, Object> params = new HashMap<>();
            params.put("nomeFuncionario", funcionario.getNome());
            params.put("mesReferencia", mes);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=relatorio_ponto.pdf");

            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao gerar relatório: " + e.getMessage());
            } catch (Exception ignored) {}
        }
    }
}
