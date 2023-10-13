package br.com.sanittas.app.service.autenticacao.dto;

import br.com.sanittas.app.model.Empresa;
import br.com.sanittas.app.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Empresa> empresaOpt = empresaRepository.findByCnpj(username);

        if (empresaOpt.isEmpty()) {

            throw new UsernameNotFoundException(String.format("usuario %s não encontrado", username));
        }
        return new EmpresaDetalhesDto(empresaOpt.get());
    }
}
