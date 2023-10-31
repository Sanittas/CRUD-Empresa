package br.com.sanittas.app.service.empresa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CNPJ;

public record EmpresaCriacaoDto(
        @NotBlank
        String razaoSocial,
        @CNPJ
        String cnpj,
        @NotBlank
        String senha,

        @NotBlank
        @Email
        String email
) {
}
