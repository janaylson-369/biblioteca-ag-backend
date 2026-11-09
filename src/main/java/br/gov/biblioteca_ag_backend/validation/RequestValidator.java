
package br.gov.biblioteca_ag_backend.validation;

import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestValidator {

    private final Validator validator;

    public RequestValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validar(T dto) {
        validar(dto, null, false, "capa");
    }

    public <T> void validar(
            T dto,
            MultipartFile capa,
            boolean capaObrigatorio,
            String nomeCampoArquivo) {

        Map<String, String> errors = new HashMap<>();

        validator.validate(dto).forEach((error) ->
                errors.put(
                        error.getPropertyPath().toString(),
                        error.getMessage()
                )
        );

        if (capaObrigatorio &&
                (capa == null || capa.isEmpty())) {

            errors.put(
                    nomeCampoArquivo,
                    "a capa é obrigatório"
            );
        }

        if (!errors.isEmpty()) {
            throw new RequestValidationException(errors);
        }
    }
}
