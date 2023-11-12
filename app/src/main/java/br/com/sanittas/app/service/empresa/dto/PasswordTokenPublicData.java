package br.com.sanittas.app.service.empresa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO contendo informações públicas associadas a um token de redefinição de senha.
 */
@Getter
@AllArgsConstructor
public class PasswordTokenPublicData {

    /**
     * Endereço de e-mail associado ao token.
     */
    private final String cnpj;

    /**
     * Timestamp indicando quando o token foi criado.
     */
    private final Long createAtTimestamp;
}
