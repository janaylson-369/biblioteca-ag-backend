package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.usuario.*;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.DadosLeitorRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final DadosLeitorRepository dadosLeitorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, DadosLeitorRepository dadosLeitorRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.dadosLeitorRepository = dadosLeitorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioRespostaDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(UsuarioRespostaDTO::fromEntity).toList();
    }

    public UsuarioResumoDTO buscarPorId(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("id usuario", String.format("usuario de id %d inexistente", id))
        );

        return UsuarioResumoDTO.fromEntity(u);
    }

    public UsuarioRespostaDTO cadastrar(CriarUsuarioDTO dto) {
        String senhaHash = passwordEncoder.encode(dto.senha_normal());

        Usuario u = new Usuario();

        u.setNome(dto.nome());
        u.setEmail(dto.email());
        u.setSenha_hash(senhaHash);
        u.setTelefone(dto.telefone());

        Usuario salvo = usuarioRepository.save(u);

        return UsuarioRespostaDTO.fromEntity(salvo);
    }

    public UsuarioRespostaDTO atualizarCompleto(Long id, AtualizarUsuarioCompletoDTO dto) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("id usuario", String.format("usuario de id %d inexistente", id))
        );

        u.setNome(dto.nome());
        u.setEmail(dto.email());
        u.setTelefone(dto.telefone());
        u.setEmail(dto.email());

        Usuario salvo = usuarioRepository.save(u);
        return UsuarioRespostaDTO.fromEntity(salvo);
    }

    public UsuarioRespostaDTO atualizar(Long id, AtualizarUsuarioDTO dto) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("id usuario", String.format("usuario de id %d inexistente", id))
        );

        if (dto.nome() != null && !dto.nome().isBlank()) {
            u.setNome(dto.nome());
        }

        if (dto.email() != null) {
            u.setEmail(dto.email());
        }

        if (dto.telefone() != null) {
            u.setTelefone(dto.telefone());
        }

        Usuario salvo = usuarioRepository.save(u);
        return UsuarioRespostaDTO.fromEntity(salvo);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("id usuario", String.format("usuario de id %d inexistente",id))
        );

        dadosLeitorRepository.delete(u.getDadosLeitor());
        usuarioRepository.deleteById(id);
    }
}
