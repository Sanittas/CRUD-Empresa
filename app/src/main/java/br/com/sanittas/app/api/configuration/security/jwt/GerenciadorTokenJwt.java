package br.com.sanittas.app.api.configuration.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GerenciadorTokenJwt {

    private static final Logger logger = LoggerFactory.getLogger(GerenciadorTokenJwt.class);

    @Value("${jwt.secret}")
    private String segredo;

    @Value("${jwt.validity}")
    private long duracaoTokenJwt;

    public String obterNomeUsuarioDoToken(String token) {
        return obterReivindicacaoDoToken(token, Claims::getSubject);
    }

    public Date obterDataExpiracaoDoToken(String token) {
        return obterReivindicacaoDoToken(token, Claims::getExpiration);
    }

    public String gerarToken(final Authentication autenticacao) {
        try {
            final String autoridades = autenticacao.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            return Jwts.builder().setSubject(autenticacao.getName())
                    .signWith(gerarChaveSecreta()).setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + duracaoTokenJwt * 1_000)).compact();
        } catch (Exception e) {
            logger.error("Erro ao gerar o token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar o token", e);
        }
    }

    public <T> T obterReivindicacaoDoToken(String token, Function<Claims, T> resolvedorReivindicacao) {
        try {
            Claims reivindicacoes = obterTodasReivindicacoesDoToken(token);
            return resolvedorReivindicacao.apply(reivindicacoes);
        } catch (Exception e) {
            logger.error("Erro ao obter reivindicações do token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao obter reivindicações do token", e);
        }
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        try {
            String nomeUsuario = obterNomeUsuarioDoToken(token);
            return (nomeUsuario.equals(userDetails.getUsername()) && !tokenExpirado(token));
        } catch (Exception e) {
            logger.error("Erro ao validar o token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao validar o token", e);
        }
    }

    public boolean tokenExpirado(String token) {
        try {
            Date expiracao = obterDataExpiracaoDoToken(token);
            return expiracao.before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            logger.error("Erro ao verificar a expiração do token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao verificar a expiração do token", e);
        }
    }

    private Claims obterTodasReivindicacoesDoToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(gerarChaveSecreta())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Erro ao analisar as reivindicações do token: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao analisar as reivindicações do token", e);
        }
    }

    private SecretKey gerarChaveSecreta() {
        try {
            return Keys.hmacShaKeyFor(this.segredo.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Erro ao gerar a chave secreta: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao gerar a chave secreta", e);
        }
    }
}
