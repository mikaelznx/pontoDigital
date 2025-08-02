package com.livroponto.controller;

import com.livroponto.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/funcionarios")
@CrossOrigin(origins = "http://localhost:5173") // altere para seu front end
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;


}
