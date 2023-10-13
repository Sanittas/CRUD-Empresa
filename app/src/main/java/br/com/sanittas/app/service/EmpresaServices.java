package br.com.sanittas.app.service;

import br.com.sanittas.app.api.configuration.security.jwt.GerenciadorTokenJwt;
import br.com.sanittas.app.model.Empresa;
import br.com.sanittas.app.model.Endereco;
import br.com.sanittas.app.repository.EmpresaRepository;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaLoginDto;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;
import br.com.sanittas.app.service.empresa.dto.*;
import br.com.sanittas.app.service.endereco.dto.ListaEndereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpresaServices {
    @Autowired
    EmpresaRepository repository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;
    @Autowired
    private AuthenticationManager authenticationManager;

    public List<ListaEmpresa> listarEmpresas() {
        List<Empresa> empresas = repository.findAll();
        List<ListaEmpresa> listaEmpresas = new ArrayList<>();
        for (Empresa empresa : empresas) {
            List<ListaEndereco> listaEnderecos = new ArrayList<>();
            extrairEndereco(empresa, listaEnderecos);
            var empresaDto = new ListaEmpresa(
                    empresa.getId(),
                    empresa.getRazaoSocial(),
                    empresa.getCnpj(),
                    listaEnderecos
            );
            listaEmpresas.add(empresaDto);
        }
        return listaEmpresas;
    }

    private static void extrairEndereco(Empresa empresa, List<ListaEndereco> listaEnderecos) {
        for(Endereco endereco : empresa.getEnderecos()){
            var enderecoDto = new ListaEndereco(
                    endereco.getId(),
                    endereco.getLogradouro(),
                    endereco.getNumero(),
                    endereco.getComplemento(),
                    endereco.getEstado(),
                    endereco.getCidade()
            );
            listaEnderecos.add(enderecoDto);
        }
    }

    public void cadastrar(EmpresaCriacaoDto empresa) {
        Empresa empresaNova = new Empresa();
        empresaNova.setRazaoSocial(empresa.razaoSocial());
        empresaNova.setCnpj(empresa.cnpj());

        repository.save(empresaNova);
    }

    public void atualizar(EmpresaCriacaoDto empresa, Integer id) {
        var empresaAtualizada = repository.findById(id);
        if (empresaAtualizada.isPresent()){
            empresaAtualizada.get().setRazaoSocial(empresa.razaoSocial());
            empresaAtualizada.get().setCnpj(empresa.cnpj());
            repository.save(empresaAtualizada.get());
        }
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }

    public EmpresaTokenDto autenticar(EmpresaLoginDto empresaLoginDto) {
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                empresaLoginDto.cnpj(), empresaLoginDto.senha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Empresa empresaAutenticada =
                repository.findByCnpj(empresaLoginDto.cnpj())
                        .orElseThrow(
                                () -> new ResponseStatusException(404, "cnpj não cadastrado", null)
                        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwtToken = gerenciadorTokenJwt.generateToken(authentication);

        return EmpresaMapper.of(empresaAutenticada, jwtToken);
    }
}
