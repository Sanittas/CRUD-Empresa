package br.com.sanittas.app.controller;

import br.com.sanittas.app.service.EmpresaServices;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaLoginDto;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;
import br.com.sanittas.app.service.empresa.dto.EmpresaCriacaoDto;
import br.com.sanittas.app.service.empresa.dto.ListaEmpresa;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaServices services;

    @GetMapping("/myproject")
    public String getRedirectUrl() {
        return "redirect:swagger-ui/";
    }

    @PostMapping("/login")
    public ResponseEntity<EmpresaTokenDto> login(@RequestBody EmpresaLoginDto empresaLoginDto) {
        EmpresaTokenDto empresaTokenDto = services.autenticar(empresaLoginDto);
        return ResponseEntity.status(200).body(empresaTokenDto);
    }

    @GetMapping("/")
    public ResponseEntity<List<ListaEmpresa>> listarEmpresas() {
        try{
            List<ListaEmpresa> response = services.listarEmpresas();
            if (!response.isEmpty()) {
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




}
