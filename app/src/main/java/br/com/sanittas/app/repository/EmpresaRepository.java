package br.com.sanittas.app.repository;

import br.com.sanittas.app.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para a entidade Empresa, responsável por operações de CRUD no banco de dados.
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    /**
     * Busca uma empresa pelo CNPJ.
     *
     * @param cnpj CNPJ da empresa a ser buscada.
     * @return Optional<Empresa> - Empresa encontrada, se existir.
     */
    Optional<Empresa> findByCnpj(String cnpj);

    boolean existsByRazaoSocial(String s);

    boolean existsByCnpj(String cnpj);
}
