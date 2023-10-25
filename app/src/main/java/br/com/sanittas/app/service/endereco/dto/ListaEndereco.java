package br.com.sanittas.app.service.endereco.dto;

public record ListaEndereco(
        Long id,
        String logradouro,
        String numero,
        String complemento,
        String estado,
        String cidade
) {
    @Override
    public String toString() {
        return String.format("%d;%s;%s;%s;%s;%s",id, logradouro, numero, complemento, estado, cidade);
    }
}
