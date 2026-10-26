
package br.gov.biblioteca_ag_backend.service;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.UUID;

@Service
public class CapaStorageService {

    private static final long LIMITE_FINAL =
            2L * 1024 * 1024; // 2 MiB

    private static final long LIMITE_ORIGINAL =
            10L * 1024 * 1024; // 10 MiB

    private final Path diretorio;

    public CapaStorageService(
            @Value("${app.upload.capas-dir}") String diretorio) {

        this.diretorio = Path.of(diretorio)
                .toAbsolutePath()
                .normalize();
    }

    public String salvar(MultipartFile arquivo) {

        validarArquivo(arquivo);

        try {
            byte[] bytesOriginais = arquivo.getBytes();

            BufferedImage imagem = ImageIO.read(
                    new ByteArrayInputStream(bytesOriginais));

            if (imagem == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O arquivo enviado não é uma imagem válida.");
            }

            if (imagem.getWidth() > 8000
                    || imagem.getHeight() > 8000) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A imagem não pode ultrapassar 8000 pixels "
                                + "de largura ou altura.");
            }

            verificarEncoderWebp();

            byte[] imagemWebp = comprimir(imagem);

            Files.createDirectories(diretorio);

            String nomeArquivo = UUID.randomUUID() + ".webp";

            Path destino = diretorio.resolve(nomeArquivo)
                    .normalize();

            if (!destino.startsWith(diretorio)) {
                throw new IllegalStateException(
                        "O caminho de destino é inválido.");
            }

            Files.write(
                    destino,
                    imagemWebp,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            );

            // Caminho relativo armazenado no banco.
            return "capas/" + nomeArquivo;

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao processar ou salvar a capa.",
                    e);
        }
    }

    private void validarArquivo(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A imagem da capa é obrigatória.");
        }

        if (arquivo.getSize() > LIMITE_ORIGINAL) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A imagem original não pode ultrapassar 10 MiB.");
        }

        String tipo = arquivo.getContentType();

        if (tipo == null
                || !(tipo.equalsIgnoreCase("image/jpeg")
                || tipo.equalsIgnoreCase("image/png"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Envie uma imagem JPEG ou PNG.");
        }
    }

    private void verificarEncoderWebp() {

        Iterator<?> escritores =
                ImageIO.getImageWritersByFormatName("webp");

        if (!escritores.hasNext()) {
            throw new IllegalStateException(
                    "Encoder WebP não encontrado. "
                            + "Verifique a dependência webp-imageio.");
        }
    }

    private byte[] comprimir(BufferedImage imagem)
            throws IOException {

        // Começa pela imagem original e tenta reduções progressivas.
        double[] escalas = {
                1.0, 0.85, 0.70, 0.55, 0.40, 0.25, 0.15
        };

        double[] qualidades = {
                0.85, 0.70, 0.55, 0.40, 0.30, 0.20
        };

        for (double escala : escalas) {
            for (double qualidade : qualidades) {

                ByteArrayOutputStream saida =
                        new ByteArrayOutputStream();

                Thumbnails.of(imagem)
                        .scale(escala)
                        .outputFormat("webp")
                        .outputQuality(qualidade)
                        .toOutputStream(saida);

                byte[] resultado = saida.toByteArray();

                if (resultado.length <= LIMITE_FINAL) {
                    return resultado;
                }
            }
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não foi possível reduzir a imagem para no máximo 2 MiB.");
    }

    public void excluir(String caminhoRelativo) {

        if (caminhoRelativo == null
                || !caminhoRelativo.matches(
                "capas/[0-9a-fA-F-]{36}\\.webp")) {

            throw new IllegalArgumentException(
                    "Caminho de capa inválido.");
        }

        // O diretório configurado já corresponde à pasta capas.
        String nomeArquivo = caminhoRelativo.substring(
                "capas/".length());

        Path arquivo = diretorio.resolve(nomeArquivo)
                .normalize();

        if (!arquivo.startsWith(diretorio)) {
            throw new IllegalArgumentException(
                    "Caminho de arquivo inválido.");
        }

        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível excluir a capa.", e);
        }
    }
}
