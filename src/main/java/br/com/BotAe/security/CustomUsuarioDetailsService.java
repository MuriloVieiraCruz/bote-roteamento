package br.com.BotAe.security;

import br.com.BotAe.model.Usuario;
import br.com.BotAe.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
        Usuario usuarioEncontrado = usuarioRepository.findByLogin(nome)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + nome));

        List<GrantedAuthority> permissoes = usuarioEncontrado.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return User.builder()
                .username(usuarioEncontrado.getNome())
                .password(usuarioEncontrado.getSenha())
                .authorities(permissoes)
                .build();
    }
}
