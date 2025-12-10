package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.CreateMedecinDTO;


import java.util.ArrayList;
import java.util.List;

public class MedecinValidator {

    public static List<String> validateCreateMedecin(CreateMedecinDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation de la spécialité
        if (dto.getSpecialite() == null || dto.getSpecialite().trim().isEmpty()) {
            errors.add("La spécialité est obligatoire");
        } else if (dto.getSpecialite().length() < 3) {
            errors.add("La spécialité doit contenir au moins 3 caractères");
        }

        // Validation du salaire
        if (dto.getSalaire() != null && dto.getSalaire() < 5000) {
            errors.add("Le salaire d'un médecin doit être au moins 5000 DH");
        }

        // Validation des années d'expérience
        if (dto.getAnneeExperience() != null && dto.getAnneeExperience() < 0) {
            errors.add("Les années d'expérience doivent être positives");
        }

        return errors;
    }

    public static List<String> validateSpecialite(String specialite) {
        List<String> errors = new ArrayList<>();

        if (specialite == null || specialite.trim().isEmpty()) {
            errors.add("La spécialité est obligatoire");
        } else if (specialite.length() < 3) {
            errors.add("La spécialité doit contenir au moins 3 caractères");
        } else if (specialite.length() > 100) {
            errors.add("La spécialité ne peut pas dépasser 100 caractères");
        }

        return errors;
    }
}