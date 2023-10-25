package br.com.sanittas.app.service;

import br.com.sanittas.app.api.configuration.security.jwt.GerenciadorTokenJwt;
import br.com.sanittas.app.model.Empresa;
import br.com.sanittas.app.model.Endereco;
import br.com.sanittas.app.repository.EmpresaRepository;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaLoginDto;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;
import br.com.sanittas.app.service.empresa.dto.*;
import br.com.sanittas.app.service.endereco.dto.ListaEndereco;
import br.com.sanittas.app.util.ListaObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.*;

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

    public ListaObj<ListaEmpresa> listarEmpresas() {
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
        Empresa empresaNova = new Empresa();
        empresaNova.setRazaoSocial(empresa.razaoSocial());
        empresaNova.setCnpj(empresa.cnpj());

        repository.save(empresaNova);
    }

    public void atualizar(EmpresaCriacaoDto empresa, Integer id) {
        var empresaAtualizada = repository.findById(id);
        if (empresaAtualizada.isPresent()) {
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

    public String gravaArquivosCsv(ListaObj<ListaEmpresa> lista) {
//        FileWriter arq = null;
//        Formatter saida = null;
//        boolean deuRuim = false;
//        String pastaDownloads = System.getProperty("user.home") + "/Downloads";
//        String nomeArq = pastaDownloads + "/resultado.csv";
//
//        try {
//            arq = new FileWriter(nomeArq);
//            saida = new Formatter(arq);
//        } catch (IOException erro) {
//            System.out.println("Erro ao abrir o arquivo");
//            throw new RuntimeException(erro);
//        }
//
//        try {
//            saida.format("id;razaoSocial;cnpj;logradouro;numero;complemento;estado;cidade\n");
//            for (int i = 0; i < lista.getNroElem(); i++) {
//                if (lista.getElemento(i).enderecos().isEmpty()) {
//                    saida.format(
//                            lista.getElemento(i).id() + ";" +
//                                    lista.getElemento(i).razaoSocial() + ";" +
//                                    lista.getElemento(i).cnpj() + "\n"
//                    );
//                } else {
//                    saida.format(
//                            lista.getElemento(i).id() + ";" +
//                                    lista.getElemento(i).razaoSocial() + ";" +
//                                    lista.getElemento(i).cnpj() + ";" +
//                                    lista.getElemento(i).enderecos().get(0).logradouro() + ";" +
//                                    lista.getElemento(i).enderecos().get(0).numero() + ";" +
//                                    lista.getElemento(i).enderecos().get(0).complemento() + ";" +
//                                    lista.getElemento(i).enderecos().get(0).estado() + ";" +
//                                    lista.getElemento(i).enderecos().get(0).cidade() + "\n"
//                    );
//                }
//            }
//
//        } catch (FormatterClosedException erro) {
//            System.out.println("Erro ao gravar o arquivo");
//            deuRuim = true;
//        } finally {
//            saida.close();
//            try {
//                arq.close();
//            } catch (IOException erro) {
//                System.out.println("Erro ao fechar o arquivo");
//                deuRuim = true;
//            }
//        }
        String csv = "id;razaoSocial;cnpj;logradouro;numero;complemento;estado;cidade\n";
        for (int i = 0; i < lista.getNroElem(); i++) {
            if (lista.getElemento(i).enderecos().isEmpty()) {
                csv +=
                        lista.getElemento(i).id() + ";" +
                                lista.getElemento(i).razaoSocial() + ";" +
                                lista.getElemento(i).cnpj() + "\n";
            } else {
                csv += lista.getElemento(i).id() + ";" +
                                lista.getElemento(i).razaoSocial() + ";" +
                                lista.getElemento(i).cnpj() + ";" +
                                lista.getElemento(i).enderecos().get(0).logradouro() + ";" +
                                lista.getElemento(i).enderecos().get(0).numero() + ";" +
                                lista.getElemento(i).enderecos().get(0).complemento() + ";" +
                                lista.getElemento(i).enderecos().get(0).estado() + ";" +
                                lista.getElemento(i).enderecos().get(0).cidade() + "\n";
            }
        }
        return csv;
    }


    public ListaObj<ListaEmpresa> ordenarPorRazaoSocial() {
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
        return listaEmpresas;
    }

    public Integer pesquisaBinariaRazaoSocial(String razaoSocial) {
        ListaObj<ListaEmpresa> listaObj = ordenarPorRazaoSocial();
        return listaObj.pesquisaBinaria(razaoSocial);
    }
}
