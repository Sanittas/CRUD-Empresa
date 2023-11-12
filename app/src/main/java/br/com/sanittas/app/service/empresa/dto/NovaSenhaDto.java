package br.com.sanittas.app.service.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * DTO utilizado para transferir informações durante o processo de alteração de senha.
 */
@Getter
public class NovaSenhaDto {

    /**
     * Nova senha que será definida.
     */
    @NotBlank
    private String novaSenha;

    /**
     * Token associado à solicitação de alteração de senha.
     */
    @NotBlank
    private String token;
}
