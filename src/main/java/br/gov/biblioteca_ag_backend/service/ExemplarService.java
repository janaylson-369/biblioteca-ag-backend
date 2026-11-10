
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.exemplar.AtualizarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.CriarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarResumoDTO;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Exemplar;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExemplarService {

    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    public ExemplarService(
            ExemplarRepository exemplarRepository,
            LivroRepository livroRepository,
            EmprestimoRepository emprestimoRepository
    ) {
        this.exemplarRepository = exemplarRepository;
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Transactional
    public ExemplarRespostaDTO cadastrar(CriarExemplarDTO dto) {

        Livro livro = livroRepository.findById(dto.id_livro())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id livro",
                        String.format("livro de id %d nao encontrado", dto.id_livro())
                ));

        if (exemplarRepository.existsByNTombamento(dto.nTombamento())) {
            throw new ResourceAlreadyExistsException(
                    "numero tombamento existente",
                    "Já existe um exemplar cadastrado com esse número de tombamento."
            );
        }

        Exemplar ex = new Exemplar();

        ex.setLivro(livro);
        ex.setStatus(StatusExemplar.DISPONIVEL);
        ex.setLocalizacaoEstante(dto.localizaoEstante());
        ex.setNTombamento(dto.nTombamento());

        Exemplar salvo = exemplarRepository.save(ex);

        return ExemplarRespostaDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<ExemplarRespostaDTO> listarTodos() {

        return exemplarRepository.findAll()
                .stream()
                .map(ExemplarRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExemplarResumoDTO buscarPorId(Long id) {

        Exemplar ex = obterExemplar(id);

        return ExemplarResumoDTO.fromEntity(ex);
    }

    @Transactional(readOnly = true)
    public List<ExemplarRespostaDTO> buscarPorLivroId(Long id_livro) {

        if (!livroRepository.existsById(id_livro)) {
            throw new ResourceNotFoundException(
                    "id livro",
                    String.format("livro de id %d inexistente", id_livro)
            );
        }

        return exemplarRepository.findByLivro_Id(id_livro)
                .stream()
                .map(ExemplarRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void excluir(Long id) {

        Exemplar ex = obterExemplar(id);

        if (emprestimoRepository.existsByExemplar_Id(id)) {
            throw new RequestValidationException(
                    "exemplar ja foi emprestado",
                    "Não é possível excluir o exemplar, pois existem empréstimos associados."
            );
        }

        exemplarRepository.delete(ex);
    }

    @Transactional
    public ExemplarResumoDTO atualizar(Long id, AtualizarExemplarDTO dto) {

        Exemplar ex = obterExemplar(id);

        if (dto.nTombamento() != null &&
                !dto.nTombamento().equals(ex.getNTombamento())) {

            if (exemplarRepository.existsByNTombamentoAndIdNot(
                    dto.nTombamento(), id
            )) {
                throw new ResourceAlreadyExistsException(
                        "numero tombamento existente",
                        "Já existe outro exemplar cadastrado com esse número de tombamento."
                );
            }

            ex.setNTombamento(dto.nTombamento());
        }

        if (dto.localizaoEstante() != null) {
            ex.setLocalizacaoEstante(dto.localizaoEstante());
        }

        return ExemplarResumoDTO.fromEntity(ex);
    }

    //========================================

    private Exemplar obterExemplar(Long id) {

        return exemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id exemplar",
                        String.format("exemplar de id %d inexistente", id)
                ));
    }
}
