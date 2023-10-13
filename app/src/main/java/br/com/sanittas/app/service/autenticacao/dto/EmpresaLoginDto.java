package br.com.sanittas.app.service.autenticacao.dto;

public record EmpresaLoginDto(
        String cnpj,
        String senha
) {
}
