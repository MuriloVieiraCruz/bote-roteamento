package br.com.BotAe.controller;

import br.com.BotAe.config.MapConverter;
import br.com.BotAe.model.Login;
import br.com.BotAe.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    @Qualifier("authServiceProxy")
    private final AuthService authService;

    private final MapConverter converter;

    public AuthController(
            @Qualifier("authServiceProxy")
            AuthService authService, MapConverter converter) {
        this.authService = authService;
        this.converter = converter;
    }

    @PostMapping("/auth")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    public ResponseEntity<Map<String, Object>> logar(@RequestBody @Valid Login login) {
        String token = authService.logar(login);
        return ResponseEntity.ok(converter.toJsonMap(token));
    }
}
