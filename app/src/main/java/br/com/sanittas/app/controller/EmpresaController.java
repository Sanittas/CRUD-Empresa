package br.com.sanittas.app.controller;

import br.com.sanittas.app.service.EmpresaServices;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaLoginDto;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;
import br.com.sanittas.app.service.empresa.dto.EmpresaCriacaoDto;
import br.com.sanittas.app.service.empresa.dto.ListaEmpresa;
import br.com.sanittas.app.util.ListaObj;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.FormatterClosedException;
import java.util.List;

@RestController
@RequestMapping("/empresas")
@SecurityRequirement(name = "bearer-key")
public class EmpresaController {

    @Autowired
    private EmpresaServices services;

    @PostMapping("/login")
    public ResponseEntity<EmpresaTokenDto> login(@RequestBody EmpresaLoginDto empresaLoginDto) {
        EmpresaTokenDto empresaTokenDto = services.autenticar(empresaLoginDto);
        return ResponseEntity.status(200).body(empresaTokenDto);
    }

    @GetMapping("/")
    public ResponseEntity<ListaObj<ListaEmpresa>> listarEmpresas() {
        try{
            ListaObj<ListaEmpresa> response = services.listarEmpresas();
            if (response.getNroElem() > 0) {
                return ResponseEntity.status(200).body(response);
            }
            return ResponseEntity.status(204).build();
        }catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<Void> cadastrarEmpresa(@RequestBody @Valid EmpresaCriacaoDto empresa) {
        try {
            services.cadastrar(empresa);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarEmpresa(@RequestBody @Valid EmpresaCriacaoDto empresa, @PathVariable Integer id) {
        try {
            services.atualizar(empresa,id);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmpresa(@PathVariable Integer id) {
        try {
            services.deletar(id);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<?> gravaArquivoCsv(HttpServletResponse response) throws IOException {
        try{
            response.setContentType("text/csv");

            response.setHeader("Content-Disposition", "attachment; filename=resultado.csv");
            String csvContent = services.gravaArquivosCsv(services.listarEmpresas());

            try (PrintWriter writer = response.getWriter()) {
                writer.write(csvContent);
            }
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e);
        }
    }

    @PostMapping("/ordenar-razao-social")
    public ResponseEntity<?> ordenarPorRazaoSocial(HttpServletResponse response) throws IOException {
        try{
            response.setContentType("text/csv");

            response.setHeader("Content-Disposition", "attachment; filename=resultado.csv");
            ListaObj<ListaEmpresa> lista = services.ordenarPorRazaoSocial();
            String csvContent = services.gravaArquivosCsv(lista);

            try (PrintWriter writer = response.getWriter()) {
                writer.write(csvContent);
            }
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e);
        }
    }

    @GetMapping("/pesquisa-razao-social/{razaoSocial}")
    public ResponseEntity<Integer> pesquisaBinariaRazaoSocial(@PathVariable String razaoSocial) {
        try{
            Integer response = services.pesquisaBinariaRazaoSocial(razaoSocial);
            return ResponseEntity.status(200).body(response);
        }catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

}
