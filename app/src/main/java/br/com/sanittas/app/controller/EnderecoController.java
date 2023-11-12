package br.com.sanittas.app.controller;

import br.com.sanittas.app.service.EmpresaServices;
import br.com.sanittas.app.service.EnderecoServices;
import br.com.sanittas.app.service.endereco.dto.EnderecoCriacaoDto;
import br.com.sanittas.app.service.endereco.dto.ListaEndereco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
@Slf4j
public class EnderecoController {

    @Autowired
    private EmpresaServices empresaServices;

    @Autowired
    private EnderecoServices enderecoServices;

    /**
     * Lista os endereços de uma empresa pelo ID.
     *
     * @param id_empresa ID da empresa.
     * @return ResponseEntity<List<ListaEndereco>> - Lista de endereços.
     */
    @GetMapping("/empresas/{id_empresa}")
    public ResponseEntity<List<ListaEndereco>> listarEnderecosPorEmpresa(@PathVariable Integer id_empresa) {
        try {
            log.info("Listando endereços da empresa com ID: {}", id_empresa);
            var response = enderecoServices.listarEnderecosPorEmpresa(id_empresa);

            if (!response.isEmpty()) {
                return ResponseEntity.status(200).body(response);
            }

            log.info("A empresa com ID {} não possui endereços cadastrados.", id_empresa);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            log.error("Erro ao listar endereços da empresa com ID: {}", id_empresa, e);
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Cadastra um novo endereço para uma empresa.
     *
     * @param endereco     DTO de criação do endereço.
     * @param empresa_id   ID da empresa.
     * @return ResponseEntity<Void> - Resposta HTTP.
     */
    @PostMapping("/empresas/{empresa_id}")
    public ResponseEntity<Void> cadastrarEnderecoEmpresa(@RequestBody EnderecoCriacaoDto endereco, @PathVariable Integer empresa_id) {
        try {
            log.info("Cadastrando endereço para a empresa com ID: {}", empresa_id);
            enderecoServices.cadastrarEnderecoEmpresa(endereco, empresa_id);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            log.error("Erro ao cadastrar endereço para a empresa com ID: {}", empresa_id, e);
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Atualiza um endereço de uma empresa.
     *
     * @param enderecoCriacaoDto DTO de atualização do endereço.
     * @param id                ID do endereço.
     * @return ResponseEntity<ListaEndereco> - Endereço atualizado.
     */
    @PutMapping("/empresas/{id}")
    public ResponseEntity<ListaEndereco> atualizarEnderecoEmpresa(@RequestBody EnderecoCriacaoDto enderecoCriacaoDto, @PathVariable Long id) {
        try {
            log.info("Atualizando endereço com ID: {}", id);
            var endereco = enderecoServices.atualizar(enderecoCriacaoDto, id);
            return ResponseEntity.status(200).body(endereco);
        } catch (Exception e) {
            log.error("Erro ao atualizar endereço com ID: {}", id, e);
            return ResponseEntity.status(400).build();
        }
    }

    /**
     * Deleta um endereço de uma empresa.
     *
     * @param id ID do endereço.
     * @return ResponseEntity<Void> - Resposta HTTP.
     */
    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> deletarEnderecoEmpresa(@PathVariable Long id) {
        try {
            log.info("Deletando endereço com ID: {}", id);
            enderecoServices.deletarEndereco(id);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            log.error("Erro ao deletar endereço com ID: {}", id, e);
            return ResponseEntity.status(400).build();
        }
    }
}
