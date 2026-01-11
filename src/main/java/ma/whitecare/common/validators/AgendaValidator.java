package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.AgendaDto.CreateAgendaDTO;
import ma.whitecare.mvc.dto.AgendaDto.UpdateAgendaDTO;

import java.util.ArrayList;
import java.util.List;

public class AgendaValidator {

    public static List<String> validateCreateAgenda(CreateAgendaDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du mois
        if (dto.getMois() == null) {
            errors.add("Le mois est obligatoire");
        }

        // Validation de l'année
        if (dto.getAnnee() == null) {
            errors.add("L'année est obligatoire");
        } else if (dto.getAnnee() < 2020 || dto.getAnnee() > 2100) {
            errors.add("L'année doit être entre 2020 et 2100");
        }

        // Validation du médecin
        if (dto.getMedecinId() == null) {
            errors.add("L'ID du médecin est obligatoire");
        } else if (dto.getMedecinId() <= 0) {
            errors.add("L'ID du médecin doit être positif");
        }

        return errors;
    }

    public static List<String> validateUpdateAgenda(UpdateAgendaDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du mois (si fourni)
        // Pas de validation spécifique, juste vérifier qu'il n'est pas null si fourni

        // Validation de l'année (si fournie)
        if (dto.getAnnee() != null) {
            if (dto.getAnnee() < 2020 || dto.getAnnee() > 2100) {
                errors.add("L'année doit être entre 2020 et 2100");
            }
        }

        // Validation du médecin (si fourni)
        if (dto.getMedecinId() != null && dto.getMedecinId() <= 0) {
            errors.add("L'ID du médecin doit être positif");
        }

        return errors;
    }
}
