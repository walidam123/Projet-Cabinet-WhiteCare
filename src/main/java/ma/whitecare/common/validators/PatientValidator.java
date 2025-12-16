package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.PatientAntecedentDto.PatientDto;

public class PatientValidator {

    public static void validate(PatientDto dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du patient est obligatoire.");
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email invalide.");
        }
        if (dto.getTelephone() == null || dto.getTelephone().length() < 6) {
            throw new IllegalArgumentException("Téléphone invalide.");
        }
        // Ajoute d'autres validations métier ici
    }
}
