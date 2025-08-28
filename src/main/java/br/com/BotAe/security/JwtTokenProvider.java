package br.com.BotAe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int expiracaoToken;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String gerarToken(Authentication authentication) {
        UserDetails detalhesDoUsuario = (UserDetails) authentication.getPrincipal();
        Date dataExpiracao = new Date(System.currentTimeMillis() + expiracaoToken * 1000L);

        return Jwts.builder()
                .subject(detalhesDoUsuario.getUsername())
                .issuedAt(new Date())
                .expiration(dataExpiracao)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameDo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token Jwt não suportado");
        } catch (IllegalArgumentException ex) {
            System.err.println("Os detalhes do token estão vazios");
        }
        return false;
    }
}
