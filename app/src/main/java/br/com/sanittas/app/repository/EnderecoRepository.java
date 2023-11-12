package br.com.sanittas.app.repository;

import br.com.sanittas.app.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositório para a entidade Endereco, responsável por operações de CRUD no banco de dados.
 */
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Pode adicionar consultas personalizadas usando consultas JPQL ou nativas SQL usando a anotação @Query se necessário.
    // Por exemplo, uma consulta JPQL personalizada:
    // @Query("SELECT e FROM Endereco e WHERE e.cidade = :cidade")
    // List<Endereco> findByCidade(String cidade);
}
