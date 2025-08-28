package br.com.BotAe.service.proxy;

import br.com.BotAe.model.Login;
import br.com.BotAe.service.AuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceProxy implements AuthService {

    @Qualifier("authServiceImpl")
    private final AuthService service;

    public AuthServiceProxy(
            @Qualifier("authServiceImpl")
            AuthService service) {
        this.service = service;
    }

    @Override
    public String logar(Login login) {
        return service.logar(login);
    }
}
