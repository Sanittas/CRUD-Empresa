package br.com.sanittas.app.service.empresa.dto;

import br.com.sanittas.app.service.endereco.dto.ListaEndereco;

import java.util.List;

public record ListaEmpresa(
        Integer id,
        String razaoSocial,
        String cnpj,
        String email,
        List<ListaEndereco> enderecos
) {
}
