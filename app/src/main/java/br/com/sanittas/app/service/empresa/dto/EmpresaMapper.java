package br.com.sanittas.app.service.empresa.dto;

import br.com.sanittas.app.model.Empresa;
import br.com.sanittas.app.service.autenticacao.dto.EmpresaTokenDto;

public class EmpresaMapper {

    public static Empresa of(EmpresaCriacaoDto empresaCriacaoDto) {
        Empresa empresa = new Empresa();

        empresa.setCnpj(empresaCriacaoDto.cnpj());
        empresa.setSenha(empresaCriacaoDto.senha());
        empresa.setRazaoSocial(empresaCriacaoDto.razaoSocial());

        return empresa;
    }

    public static EmpresaTokenDto of(Empresa empresa, String token) {
        EmpresaTokenDto empresaTokenDto = new EmpresaTokenDto();

        empresaTokenDto.setEnterpriseId(empresa.getId());
        empresaTokenDto.setRazaoSocial(empresa.getRazaoSocial());
        empresaTokenDto.setCnpj(empresa.getCnpj());
        empresaTokenDto.setToken(token);


        return empresaTokenDto;
    }
}
