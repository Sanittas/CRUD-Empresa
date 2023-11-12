package br.com.sanittas.app.service;

import br.com.sanittas.app.exception.ValidacaoException;
import br.com.sanittas.app.model.Endereco;
import br.com.sanittas.app.repository.EmpresaRepository;
import br.com.sanittas.app.repository.EnderecoRepository;
import br.com.sanittas.app.service.endereco.dto.EnderecoCriacaoDto;
import br.com.sanittas.app.service.endereco.dto.EnderecoMapper;
import br.com.sanittas.app.service.endereco.dto.ListaEndereco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnderecoServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnderecoServices.class);

    @Autowired
    private EnderecoRepository repository;

    @Autowired
    private EmpresaRepository empresaRepository;

    public void cadastrarEnderecoEmpresa(EnderecoCriacaoDto enderecoCriacaoDto, Integer empresa_id) {
        LOGGER.info("Cadastrando endereço para a empresa com ID: {}", empresa_id);
        var endereco = EnderecoMapper.of(enderecoCriacaoDto);
        var empresa = empresaRepository.findById(empresa_id);

        if (empresa.isPresent()) {
            endereco.setEmpresa(empresa.get());
            repository.save(endereco);
            LOGGER.info("Endereço cadastrado com sucesso para a empresa com ID: {}", empresa_id);
        } else {
            LOGGER.warn("Tentativa de cadastrar endereço para empresa com ID {}, mas a empresa não foi encontrada.", empresa_id);
        }
    }

    public ListaEndereco atualizar(EnderecoCriacaoDto enderecoCriacaoDto, Long id) {
        LOGGER.info("Atualizando endereço com ID: {}", id);
        var endereco = repository.findById(id);

        if (endereco.isPresent()) {
            endereco.get().setLogradouro(enderecoCriacaoDto.getLogradouro());
            endereco.get().setNumero(enderecoCriacaoDto.getNumero());
            endereco.get().setComplemento(enderecoCriacaoDto.getComplemento());
            endereco.get().setEstado(enderecoCriacaoDto.getEstado());
            endereco.get().setCidade(enderecoCriacaoDto.getCidade());
            repository.save(endereco.get());

            LOGGER.info("Endereço atualizado com sucesso. ID: {}", id);

            return new ListaEndereco(
                    endereco.get().getId(),
                    endereco.get().getLogradouro(),
                    endereco.get().getNumero(),
                    endereco.get().getComplemento(),
                    endereco.get().getEstado(),
                    endereco.get().getCidade()
            );
        } else {
            LOGGER.warn("Tentativa de atualizar endereço com ID {}, mas não encontrado.", id);
            throw new ValidacaoException("Endereço não encontrado");
        }
    }

    public void deletarEndereco(Long id) {
        LOGGER.info("Deletando endereço com ID: {}", id);

        if (repository.existsById(id)) {
            repository.deleteById(id);
            LOGGER.info("Endereço deletado com sucesso. ID: {}", id);
        } else {
            LOGGER.warn("Tentativa de deletar endereço com ID {}, mas não encontrado.", id);
            throw new ValidacaoException("Endereço não existe!");
        }
    }

    public List<ListaEndereco> listarEnderecosPorEmpresa(Integer idEmpresa) {
        LOGGER.info("Listando endereços para a empresa com ID: {}", idEmpresa);
        var empresa = empresaRepository.findById(idEmpresa);
        List<ListaEndereco> enderecos = new ArrayList<>();

        if (empresa.isPresent()) {
            for (Endereco endereco : empresa.get().getEnderecos()) {
                var enderecoDto = new ListaEndereco(
                        endereco.getId(),
                        endereco.getLogradouro(),
                        endereco.getNumero(),
                        endereco.getComplemento(),
                        endereco.getEstado(),
                        endereco.getCidade()
                );
                enderecos.add(enderecoDto);
            }

            LOGGER.info("Endereços listados com sucesso para a empresa com ID: {}", idEmpresa);
            return enderecos;
        } else {
            LOGGER.warn("Tentativa de listar endereços para empresa com ID {}, mas não encontrada.", idEmpresa);
            return null;
        }
    }
}
