package br.com.sanittas.app.service.endereco.dto;

public record ListaEndereco(
        Long id,
        String logradouro,
        String numero,
        String complemento,
        String estado,
        String cidade
) {
}
