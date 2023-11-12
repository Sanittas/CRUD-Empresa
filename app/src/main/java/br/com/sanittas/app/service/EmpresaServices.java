package br.com.sanittas.app.service;

import br.com.sanittas.app.api.configuration.security.jwt.GerenciadorTokenJwt;
import br.com.sanittas.app.exception.ValidacaoException;
import br.com.sanittas.app.model.Empresa;
import br.com.sanittas.app.model.Endereco;
import br.com.sanittas.app.repository.EmpresaRepository;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaLoginDto;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;
import br.com.sanittas.app.service.empresa.dto.*;
import br.com.sanittas.app.service.endereco.dto.ListaEndereco;
import br.com.sanittas.app.util.ListaObj;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class EmpresaServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmpresaServices.class);

    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ListaObj<ListaEmpresa> listarEmpresas() {
        LOGGER.info("Listando todas as empresas.");
        List<Empresa> empresas = repository.findAll();
        ListaObj<ListaEmpresa> listaEmpresas = new ListaObj<>(empresas.size());
        for (Empresa empresa : empresas) {
            List<ListaEndereco> listaEnderecos = new ArrayList<>();
            extrairEndereco(empresa, listaEnderecos);
            var empresaDto = new ListaEmpresa(
                    empresa.getId(),
                    empresa.getRazaoSocial(),
                    empresa.getCnpj(),
                    listaEnderecos
            );
            listaEmpresas.adiciona(empresaDto);
        }
        LOGGER.info("Empresas listadas com sucesso.");
        return listaEmpresas;
    }

    private static void extrairEndereco(Empresa empresa, List<ListaEndereco> listaEnderecos) {
        for (Endereco endereco : empresa.getEnderecos()) {
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
        if (repository.existsByRazaoSocial(empresa.razaoSocial())) {
         throw new ValidacaoException("Razão Social já cadastrada.");
        }
        if (repository.existsByCnpj(empresa.cnpj())) {
            throw new ValidacaoException("CNPJ já cadastrado.");
        }
        LOGGER.info("Cadastrando nova empresa.");
        Empresa empresaNova = new Empresa();
        empresaNova.setRazaoSocial(empresa.razaoSocial());
        empresaNova.setCnpj(empresa.cnpj());


        repository.save(empresaNova);
        LOGGER.info("Empresa cadastrada com sucesso.");
    }

    public void atualizar(EmpresaCriacaoDto empresa, Integer id) {
        LOGGER.info("Atualizando empresa com ID: {}", id);
        var empresaAtualizada = repository.findById(id);
        if (empresaAtualizada.isPresent()) {
            empresaAtualizada.get().setRazaoSocial(empresa.razaoSocial());
            empresaAtualizada.get().setCnpj(empresa.cnpj());
            repository.save(empresaAtualizada.get());
            LOGGER.info("Empresa atualizada com sucesso.");
        } else {
            LOGGER.warn("Tentativa de atualizar empresa com ID {}, mas não encontrada.", id);
        }
    }

    public void deletar(Integer id) {
        LOGGER.info("Deletando empresa com ID: {}", id);
        repository.deleteById(id);
        LOGGER.info("Empresa deletada com sucesso.");
    }

    public EmpresaTokenDto autenticar(EmpresaLoginDto empresaLoginDto) {
        LOGGER.info("Autenticando empresa com CNPJ: {}", empresaLoginDto.cnpj());
        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                empresaLoginDto.cnpj(), empresaLoginDto.senha());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);

        Empresa empresaAutenticada =
                repository.findByCnpj(empresaLoginDto.cnpj())
                        .orElseThrow(
                                () -> new ResponseStatusException(404, "CNPJ não cadastrado", null)
                        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwtToken = gerenciadorTokenJwt.gerarToken(authentication);

        LOGGER.info("Autenticação bem-sucedida para empresa com CNPJ: {}", empresaLoginDto.cnpj());

        return EmpresaMapper.of(empresaAutenticada, jwtToken);
    }

    public void gravaArquivosCsv(ListaObj<ListaEmpresa> lista) {
        LOGGER.info("Gravando arquivo CSV com informações das empresas.");
        FileWriter arq = null;
        PrintWriter saida = null;
        boolean deuRuim = false;
        String pastaDownloads = System.getProperty("user.home") + "/Downloads";
        String nomeArq = pastaDownloads + "/resultado.csv";

        try {
            arq = new FileWriter(nomeArq);
            saida = new PrintWriter(arq);
        } catch (IOException erro) {
            LOGGER.error("Erro ao abrir o arquivo.", erro);
            System.exit(1);
        }

        try {
            saida.println("ID;Razão Social;CNPJ;Logradouro;Número;Complemento;Estado;Cidade;");
            for (int i = 0; i < lista.getNroElem(); i++) {
                if (lista.getElemento(i).enderecos().isEmpty()) {
                    saida.println(
                            lista.getElemento(i).id() + ";" +
                                    lista.getElemento(i).razaoSocial() + ";" +
                                    lista.getElemento(i).cnpj() + ";"
                    );
                } else {
                    saida.println(
                            lista.getElemento(i).id() + ";" +
                                    lista.getElemento(i).razaoSocial() + ";" +
                                    lista.getElemento(i).cnpj() + ";" +
                                    lista.getElemento(i).enderecos().get(0).logradouro() + ";" +
                                    lista.getElemento(i).enderecos().get(0).numero() + ";" +
                                    lista.getElemento(i).enderecos().get(0).complemento() + ";" +
                                    lista.getElemento(i).enderecos().get(0).estado() + ";" +
                                    lista.getElemento(i).enderecos().get(0).cidade()
                    );
                }
            }

        } catch (FormatterClosedException erro) {
            LOGGER.error("Erro ao gravar o arquivo.", erro);
            deuRuim = true;
        } finally {
            saida.close();
            try {
                arq.close();
            } catch (IOException erro) {
                LOGGER.error("Erro ao fechar o arquivo.", erro);
                deuRuim = true;
            }
            if (deuRuim) {
                System.exit(1);
            }
        }
        LOGGER.info("Arquivo CSV gravado com sucesso.");
    }

    public ListaObj<ListaEmpresa> ordenarPorRazaoSocial() {
        LOGGER.info("Ordenando empresas por Razão Social.");
        ListaObj<ListaEmpresa> listaEmpresas = listarEmpresas();
        for (int i = 0; i < listaEmpresas.getNroElem() - 1; i++) {
            for (int j = i + 1; j < listaEmpresas.getNroElem(); j++) {
                if (listaEmpresas.getElemento(j).razaoSocial().compareToIgnoreCase(listaEmpresas.getElemento(i).razaoSocial()) < 0) {
                    ListaEmpresa aux = listaEmpresas.getElemento(i);
                    listaEmpresas.setElemento(i, listaEmpresas.getElemento(j));
                    listaEmpresas.setElemento(j, aux);
                }
            }
        }
        gravaArquivosCsv(listaEmpresas);
        LOGGER.info("Empresas ordenadas por Razão Social.");
        return listaEmpresas;
    }

    public Integer pesquisaBinariaRazaoSocial(String razaoSocial) {
        LOGGER.info("Realizando pesquisa binária por Razão Social: {}", razaoSocial);
        ListaObj<ListaEmpresa> listaObj = ordenarPorRazaoSocial();
        Integer resultado = listaObj.pesquisaBinaria(razaoSocial);
        LOGGER.info("Resultado da pesquisa binária: {}", resultado);
        return resultado;
    }

    @SneakyThrows
    public String generateToken(String cnpj) {
        try {
            LOGGER.info("Gerando token para o email: {}", cnpj);

            Empresa empresa = repository.findByCnpj(cnpj)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            KeyBasedPersistenceTokenService tokenService = getInstanceFor(empresa);
            Token token = tokenService.allocateToken(empresa.getCnpj());

            return token.getKey();
        } catch (Exception e) {
            LOGGER.error("Erro ao gerar token: {}", e.getMessage());
            throw e;
        }
    }

    @SneakyThrows
    public void validarToken(String token) {
        try {
            LOGGER.info("Validando token");

            PasswordTokenPublicData publicData = readPublicData(token);

            if (isExpired(publicData)) {
                throw new RuntimeException("Token expirado");
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao validar token: {}", e.getMessage());
            throw e;
        }
    }

    @SneakyThrows
    public void alterarSenha(NovaSenhaDto novaSenhaDto) {
        try {
            LOGGER.info("Alterando senha com token");

            PasswordTokenPublicData publicData = readPublicData(novaSenhaDto.getToken());

            if (isExpired(publicData)) {
                throw new RuntimeException("Token expirado");
            }

            Empresa empresa = repository.findByCnpj(publicData.getCnpj())
                    .orElseThrow(RuntimeException::new);

            KeyBasedPersistenceTokenService tokenService = this.getInstanceFor(empresa);
            tokenService.verifyToken(novaSenhaDto.getToken());

            empresa.setSenha(this.passwordEncoder.encode(novaSenhaDto.getNovaSenha()));
            repository.save(empresa);
        } catch (Exception e) {
            LOGGER.error("Erro ao alterar senha: {}", e.getMessage());
            throw e;
        }
    }

    private boolean isExpired(PasswordTokenPublicData publicData) {
        Instant createdAt = new Date(publicData.getCreateAtTimestamp()).toInstant();
        Instant now = new Date().toInstant();
        return createdAt.plus(Duration.ofMinutes(5)).isBefore(now);
    }

    private KeyBasedPersistenceTokenService getInstanceFor(Empresa empresa) throws Exception {
        KeyBasedPersistenceTokenService tokenService = new KeyBasedPersistenceTokenService();
        tokenService.setServerSecret(empresa.getSenha());
        tokenService.setServerInteger(16);
        tokenService.setSecureRandom(new SecureRandomFactoryBean().getObject());
        return tokenService;
    }

    private PasswordTokenPublicData readPublicData(String rawToken) {
        String rawTokenDecoded = new String(Base64.getDecoder().decode(rawToken));
        String[] tokenParts = rawTokenDecoded.split(":");
        Long timestamp = Long.parseLong(tokenParts[0]);
        String email = tokenParts[2];
        return new PasswordTokenPublicData(email, timestamp);
    }
}
