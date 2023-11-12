package br.com.sanittas.app.api.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AutenticacaoEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacaoEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        try {
            if (authException.getClass().equals(BadCredentialsException.class)) {
                logger.error("Tentativa de acesso não autorizado: credenciais inválidas");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                logger.error("Acesso não autorizado: {}", authException.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso não autorizado");
            }
        } catch (IOException e) {
            logger.error("Erro ao lidar com a entrada de autenticação: {}", e.getMessage(), e);
            throw e;
        }
    }
}
