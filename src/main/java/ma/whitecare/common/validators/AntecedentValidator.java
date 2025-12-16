package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.PatientAntecedentDto.AntecedentDto;

public class AntecedentValidator {

    public static void validate(AntecedentDto dto) {
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'antécédent est obligatoire.");
        }
        if (dto.getCategorie() == null || dto.getCategorie().isBlank()) {
            throw new IllegalArgumentException("La catégorie de l'antécédent est obligatoire.");
        }
        if (dto.getNiveauDeRisque() == null) {
            throw new IllegalArgumentException("Le niveau de risque est obligatoire.");
        }
    }
}
