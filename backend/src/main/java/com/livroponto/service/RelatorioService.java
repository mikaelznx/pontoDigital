package com.livroponto.service;

import com.livroponto.model.Funcionario;
import com.livroponto.model.RegistroPonto;
import com.livroponto.repository.FuncionarioRepository;
import com.livroponto.repository.RegistroPontoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        // Buscar registros de entrada existentes
        List<RegistroPonto> registrosEntrada = pontoRepository
                .findByFuncionarioIdAndDataBetweenOrderByDataAscHoraAsc(funcionarioId, inicio, fim);

        // Gerar dados agrupados (entrada e saída na mesma linha)
        List<RegistroRelatorio> dadosRelatorio = gerarDadosAgrupados(registrosEntrada, funcionario.getCargaHoraria());

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nomeFuncionario", funcionario.getNome());
        parametros.put("mesReferencia", formatarMes(mes) + "/" + ano);

        // Caminho absoluto das imagens
        String caminhoLogo1 = new File("C:\\ponto-digital\\backend\\src\\main\\resources\\relatorios\\logo.png").getAbsolutePath();
        String caminhoLogo2 = new File("C:\\ponto-digital\\backend\\src\\main\\resources\\relatorios\\logo2.png").getAbsolutePath();

        parametros.put("logoPath", caminhoLogo1);
        parametros.put("logo2Path", caminhoLogo2);

        InputStream reportStream = new ClassPathResource("relatorios/relatorio_ponto.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dadosRelatorio);

        return JasperFillManager.fillReport(jasperReport, parametros, dataSource);
    }

    private List<RegistroRelatorio> gerarDadosAgrupados(List<RegistroPonto> registrosEntrada, BigDecimal cargaHoraria) {
        List<RegistroRelatorio> dadosRelatorio = new ArrayList<>();

        for (RegistroPonto registro : registrosEntrada) {
            // Calcular horário de saída
            LocalTime horaSaida = calcularHorarioSaida(registro.getHora(), cargaHoraria);

            // Criar registro agrupado (entrada e saída na mesma linha)
            RegistroRelatorio relatorio = new RegistroRelatorio();
            relatorio.setData(registro.getData());

            // Formatar horários como "08:00 - 16:15"
            String horariosFormatados = registro.getHora().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " +
                    horaSaida.format(DateTimeFormatter.ofPattern("HH:mm"));
            relatorio.setHoraFormatada(horariosFormatados);

            // Tipo como "ENTRADA - SAÍDA"
            relatorio.setTipo("ENTRADA - SAÍDA");

            // Manter compatibilidade com template (usar hora de entrada como referência)
            relatorio.setHora(registro.getHora());

            dadosRelatorio.add(relatorio);
        }

        return dadosRelatorio;
    }

    private LocalTime calcularHorarioSaida(LocalTime horaEntrada, BigDecimal cargaHoraria) {
        // Converter carga horária para minutos
        int minutosBase = cargaHoraria.multiply(BigDecimal.valueOf(60)).intValue();

        // Adicionar variação aleatória de -15 a +30 minutos para parecer mais real
        int variacaoMinutos = ThreadLocalRandom.current().nextInt(-8, 9);
        int minutosTotal = minutosBase + variacaoMinutos;

        return horaEntrada.plusMinutes(minutosTotal);
    }

    private String formatarMes(int mes) {
        String[] nomes = {
                "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return nomes[mes - 1];
    }

    // Classe que mantém compatibilidade com seu template mas com horários agrupados
    public static class RegistroRelatorio {
        private LocalDate data;
        private LocalTime hora;
        private String tipo;
        private String horaFormatada; // Novo campo para "08:00 - 16:15"

        // Getters e Setters
        public LocalDate getData() { return data; }
        public void setData(LocalDate data) { this.data = data; }

        public LocalTime getHora() { return hora; }
        public void setHora(LocalTime hora) { this.hora = hora; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getHoraFormatada() { return horaFormatada; }
        public void setHoraFormatada(String horaFormatada) { this.horaFormatada = horaFormatada; }
    }
}