package com.livroponto.controller;

import com.livroponto.service.RelatorioService;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/relatorios")
@CrossOrigin(origins = "http://localhost:5173")  // ajuste conforme seu frontend
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/mensal")
    public ResponseEntity<byte[]> gerarRelatorioPdf(
            @RequestParam Long funcionarioId,
            @RequestParam int mes,
            @RequestParam int ano) {

        try {
            JasperPrint jasperPrint = relatorioService.gerarRelatorioMensal(funcionarioId, mes, ano);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=relatorio_ponto.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage().getBytes());
        }
    }
}
