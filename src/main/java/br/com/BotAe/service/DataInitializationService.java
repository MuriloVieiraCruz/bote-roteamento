package br.com.BotAe.service;

import br.com.BotAe.model.Usuario;
import br.com.BotAe.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DataInitializationService implements CommandLineRunner {

    private final UsuarioRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public DataInitializationService(UsuarioRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initializeDefaultUser();
    }

    private void initializeDefaultUser() {
        if (userRepository.findByLogin("admin").isEmpty()) {
            Usuario defaultUser = new Usuario();
            defaultUser.setNome("Administrador");
            defaultUser.setLogin("admin");
            defaultUser.setSenha(passwordEncoder.encode("admin123"));
            defaultUser.setRoles(Arrays.asList("ADMIN", "USER"));

            userRepository.save(defaultUser);
            System.out.println("Usuário padrão criado: admin/admin123");
        }
    }
}
