package br.com.sanittas.app.api.configuration.security;

import br.com.sanittas.app.service.autenticacao.dto.AutenticacaoService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.logging.Logger;

public class AutenticacaoProvider implements AuthenticationProvider {

    private static final Logger LOGGER = Logger.getLogger(AutenticacaoProvider.class.getName());
    private final AutenticacaoService autenticacaoService;
    private final PasswordEncoder passwordEncoder;

    public AutenticacaoProvider(AutenticacaoService autenticacaoService, PasswordEncoder passwordEncoder) {
        this.autenticacaoService = autenticacaoService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        LOGGER.info("Autenticando empresa: " + username);
        UserDetails userDetails = this.autenticacaoService.loadUserByUsername(username);

        if (userDetails != null && this.passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
            LOGGER.warning("Credenciais inválidas para a empresa: " + username);
            throw new BadCredentialsException("CNPJ ou senha inválidos");
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
