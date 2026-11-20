package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.exemplar.AtualizarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.CriarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarResumoDTO;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Exemplar;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExemplarService {
    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;

    public ExemplarService(ExemplarRepository exemplarRepository, LivroRepository livroRepository) {
        this.exemplarRepository = exemplarRepository;
        this.livroRepository = livroRepository;
    }

    public ExemplarRespostaDTO cadastrar(CriarExemplarDTO dto) {

        Livro livro = livroRepository.findById(dto.id_livro()).orElseThrow(() -> new ResourceNotFoundException("id livro", String.format("livro de id %d nao encontrado", dto.id_livro())));

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
        return exemplarRepository.findAll().stream().map(ExemplarRespostaDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ExemplarResumoDTO buscarPorId(Long id) {
        Exemplar ex = exemplarRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("id exemplar", String.format("exemplar de id %s inexistente", id)));

        return ExemplarResumoDTO.fromEntity(ex);
    }

    @Transactional(readOnly = true)
    public List<ExemplarRespostaDTO> buscarPorLivroId(Long id_livro) {
        if (!livroRepository.existsById(id_livro))
            throw new ResourceNotFoundException("id livro", String.format("livro de id %d inexistente", id_livro));

        return exemplarRepository.findByLivro_Id(id_livro).
                stream().
                map(ExemplarRespostaDTO::fromEntity).
                toList();
    }

    @Transactional
    public ExemplarResumoDTO atualizar(Long id, AtualizarExemplarDTO dto) {
        Exemplar exemplar = exemplarRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("id exemplar",String.format("exemplar de id %d inexistente",id))
        );

        if (dto.id_livro() != null && dto.id_livro() > 0) {
            Livro l = livroRepository.findById(dto.id_livro()).orElseThrow(
                    ()-> new ResourceNotFoundException("id livro",String.format("livro de id %d inexistente", dto.id_livro()))
            );
            exemplar.setLivro(l);
        }

        exemplar.setNTombamento(dto.nTombamento());
        exemplar.setLocalizacaoEstante(dto.localizaoEstante());
        Exemplar salvo = exemplarRepository.save(exemplar);
        return ExemplarResumoDTO.fromEntity(salvo);
    }

    public void excluir(Long id) {
        Exemplar ex = exemplarRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("id exemplar", String.format("exemplar de id %d inexistente", id))
        );

        exemplarRepository.delete(ex);
    }
}
