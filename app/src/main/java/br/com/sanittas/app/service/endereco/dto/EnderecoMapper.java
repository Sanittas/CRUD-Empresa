package br.com.sanittas.app.service.endereco.dto;

import br.com.sanittas.app.model.Endereco;

public class EnderecoMapper {

    public static Endereco of(EnderecoCriacaoDto enderecoCriacaoDto) {
        Endereco endereco = new Endereco();

        endereco.setLogradouro(enderecoCriacaoDto.getLogradouro());
        endereco.setNumero(enderecoCriacaoDto.getNumero());
        endereco.setComplemento(enderecoCriacaoDto.getComplemento());
        endereco.setCidade(enderecoCriacaoDto.getCidade());
        endereco.setEstado(enderecoCriacaoDto.getEstado());

        return endereco;
    }
}
