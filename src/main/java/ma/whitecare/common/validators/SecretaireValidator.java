package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.UserDto.CreateSecretaireDTO;


import java.util.ArrayList;
import java.util.List;

public class SecretaireValidator {

    public static List<String> validateCreateSecretaire(CreateSecretaireDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du numéro CNSS
        if (dto.getNumCNSS() == null || dto.getNumCNSS().trim().isEmpty()) {
            errors.add("Le numéro CNSS est obligatoire");
        } else if (!dto.getNumCNSS().matches("^[A-Za-z0-9]{8,20}$")) {
            errors.add("Le numéro CNSS doit contenir 8 à 20 caractères alphanumériques");
        }

        // Validation de la commission
        if (dto.getCommission() != null && dto.getCommission() < 0) {
            errors.add("La commission doit être positive");
        }

        // Validation du salaire
        if (dto.getSalaire() != null && dto.getSalaire() < 2420) {
            errors.add("Le salaire doit être au moins égal au SMIC (2420 DH)");
        }

        return errors;
    }

    public static List<String> validateCommission(Double commission) {
        List<String> errors = new ArrayList<>();

        if (commission != null && commission < 0) {
            errors.add("La commission doit être un nombre positif");
        }

        return errors;
    }
}