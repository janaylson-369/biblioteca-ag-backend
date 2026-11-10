
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.usuario.*;
import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.DadosLeitorRepository;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
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
    private final EmprestimoRepository emprestimoRepository;
    private final ReservaRepository reservaRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            DadosLeitorRepository dadosLeitorRepository,
            PasswordEncoder passwordEncoder,
            EmprestimoRepository emprestimoRepository,
            ReservaRepository reservaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.dadosLeitorRepository = dadosLeitorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emprestimoRepository = emprestimoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioRespostaDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResumoDTO buscarPorId(Long id) {
        Usuario usuario = obterUsuario(id);
        return UsuarioResumoDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioRespostaDTO cadastrar(CriarUsuarioDTO dto) {

        validarEmailDisponivel(dto.email());

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setEmail(dto.email());
        usuario.setSenha_hash(passwordEncoder.encode(dto.senha_normal()));

        usuario.setTipoUsuario(TipoUsuario.LEITOR);

        Usuario salvo = usuarioRepository.save(usuario);

        return UsuarioRespostaDTO.fromEntity(salvo);
    }

    @Transactional
    public UsuarioRespostaDTO atualizarCompleto(
            Long id,
            AtualizarUsuarioCompletoDTO dto
    ) {
        Usuario usuario = obterUsuario(id);

        validarEmailDisponivel(dto.email(), id);

        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        usuario.setEmail(dto.email());

        return UsuarioRespostaDTO.fromEntity(usuario);
    }

    @Transactional
    public UsuarioRespostaDTO atualizar(
            Long id,
            AtualizarUsuarioDTO dto
    ) {
        Usuario usuario = obterUsuario(id);

        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }

        if (dto.telefone() != null) {
            usuario.setTelefone(dto.telefone());
        }

        if (dto.email() != null) {
            validarEmailDisponivel(dto.email(), id);
            usuario.setEmail(dto.email());
        }

        return UsuarioRespostaDTO.fromEntity(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = obterUsuario(id);

        boolean possuiEmprestimos =
                emprestimoRepository.existsByUsuario_Id(id);

        boolean possuiReservas =
                reservaRepository.existsByUsuario_Id(id);

        if (possuiEmprestimos || possuiReservas) {
            throw new RequestValidationException(
                    "usuario possui reservas ou emprestimos",
                    "Não é possível excluir o usuário, pois existem empréstimos ou reservas associados."
            );
        }

        if (usuario.getDadosLeitor() != null) {
            dadosLeitorRepository.delete(usuario.getDadosLeitor());
        }

        usuarioRepository.delete(usuario);
    }


    //==============================
    private Usuario obterUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id usuario",
                        String.format("usuario de id %d inexistente", id)
                ));
    }

    private void validarEmailDisponivel(String email) {
        if (email != null && usuarioRepository.existsByEmail(email)) {
            throw new RequestValidationException(
                    "email",
                    "Este endereço de e-mail já está cadastrado."
            );
        }
    }

    private void validarEmailDisponivel(String email, Long id) {
        if (email != null &&
                usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new RequestValidationException(
                    "email",
                    "Este endereço de e-mail já está cadastrado por outro usuário."
            );
        }
    }
}
