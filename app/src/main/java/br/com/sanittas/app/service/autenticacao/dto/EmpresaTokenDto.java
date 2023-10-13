package br.com.sanittas.app.service.autenticacao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaTokenDto {
    private Integer enterpriseId;
    private String razaoSocial;
    private String cnpj;
    private String token;
}
