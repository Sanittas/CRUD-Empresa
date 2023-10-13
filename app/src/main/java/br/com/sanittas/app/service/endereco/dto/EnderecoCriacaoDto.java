package br.com.sanittas.app.service.endereco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoCriacaoDto {
    @NotBlank
    private String logradouro;
    private String numero;
    private String complemento;
    @NotBlank
    private String cidade;
    @NotBlank
    private String estado;

}
