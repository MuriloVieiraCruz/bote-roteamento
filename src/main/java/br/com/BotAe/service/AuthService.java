package br.com.BotAe.service;

import br.com.BotAe.model.Login;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AuthService {

    String logar(Login login);
}
