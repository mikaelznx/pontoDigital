package com.livroponto.config;

import com.livroponto.model.User;
import com.livroponto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserCreator implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String username = "seuUser"; // crie um usuario
        String senha = "suaSenha"; //crie um usuario

        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(senha));
            admin.setRoles("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Usuário administrador criado");
        } else {
            System.out.println("ℹ️ Usuário administrador já existe.");
        }
    }
}
