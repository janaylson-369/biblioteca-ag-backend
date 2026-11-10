package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.dadosLeitor.AtualizarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.CriarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.DadosLeitorCompletoDTO;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.DadosLeitorRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DadosLeitorService {

    private final DadosLeitorRepository dadosLeitorRepository;
    private final UsuarioRepository usuarioRepository;

    public DadosLeitorService(
            DadosLeitorRepository dadosLeitorRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.dadosLeitorRepository = dadosLeitorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public DadosLeitorCompletoDTO cadastrarDados(
            Long id_usuario,
            CriarDadosLeitorDTO dto
    ) {

        Usuario u = obterUsuario(id_usuario);

        if (dadosLeitorRepository.existsByUsuario_Id(id_usuario)) {
            throw new ResourceAlreadyExistsException(
                    "dados leitor existente",
                    String.format(
                            "usuario de id %d já possui dados de leitor cadastrados, utilize 'atualizar dados'",
                            id_usuario
                    )
            );
        }

        DadosLeitor dl = new DadosLeitor(
                u,
                dto.dataNascimento(),
                dto.logradouro(),
                dto.numero(),
                dto.complemento(),
                dto.bairro()
        );

        // Sincroniza o lado inverso do relacionamento.
        u.setDadosLeitor(dl);

        DadosLeitor salvo = dadosLeitorRepository.save(dl);

        return DadosLeitorCompletoDTO.fromEntity(u, salvo);
    }

    @Transactional(readOnly = true)
    public DadosLeitorCompletoDTO buscarPorUsuarioId(Long id_usuario) {

        Usuario u = obterUsuario(id_usuario);

        DadosLeitor dl = u.getDadosLeitor();

        if (dl == null) {
            throw new ResourceNotFoundException(
                    "dados leitor",
                    String.format(
                            "usuario de id %d não possui dados de leitor cadastrados",
                            id_usuario
                    )
            );
        }

        return DadosLeitorCompletoDTO.fromEntity(u, dl);
    }

    @Transactional
    public DadosLeitorCompletoDTO atualizar(
            Long id_usuario,
            AtualizarDadosLeitorDTO dto
    ) {

        Usuario u = obterUsuario(id_usuario);

        DadosLeitor dl = u.getDadosLeitor();

        if (dl == null) {
            throw new ResourceNotFoundException(
                    "dados leitor",
                    "usuario sem dados de leitor, impossivel atualizar"
            );
        }

        if (dto.dataNascimento() != null)
            dl.setDataNascimento(dto.dataNascimento());

        if (dto.bairro() != null)
            dl.setBairro(dto.bairro());

        if (dto.complemento() != null)
            dl.setComplemento(dto.complemento());

        if (dto.logradouro() != null)
            dl.setLogradouro(dto.logradouro());

        if (dto.numero() != null)
            dl.setNumero(dto.numero());

        return DadosLeitorCompletoDTO.fromEntity(u, dl);
    }

    //=======================================
    private Usuario obterUsuario(Long id_usuario) {
        return usuarioRepository.findById(id_usuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id usuario",
                        String.format(
                                "usuario de id %d inexistente",
                                id_usuario
                        )
                ));
    }
}