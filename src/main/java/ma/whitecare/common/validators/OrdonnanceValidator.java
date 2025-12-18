package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.OrdonnanceDto.CreateOrdonnanceDTO;
import ma.whitecare.mvc.dto.OrdonnanceDto.UpdateOrdonnanceDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceValidator {

    public static List<String> validateCreateOrdonnance(CreateOrdonnanceDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation de la date
        if (dto.getDate() == null) {
            errors.add("La date de l'ordonnance est obligatoire");
        } else if (dto.getDate().isAfter(LocalDate.now())) {
            errors.add("La date de l'ordonnance ne peut pas être dans le futur");
        }

        // Validation du dossier médical (au moins un des deux doit être présent)
        if (dto.getDossierMedicaleId() == null && dto.getConsultationId() == null) {
            errors.add("Au moins un dossier médical ou une consultation doit être spécifié");
        }

        // Validation du dossier médical
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation de la consultation
        if (dto.getConsultationId() != null && dto.getConsultationId() <= 0) {
            errors.add("L'ID de la consultation doit être positif");
        }

        return errors;
    }

    public static List<String> validateUpdateOrdonnance(UpdateOrdonnanceDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation de la date
        if (dto.getDate() != null && dto.getDate().isAfter(LocalDate.now())) {
            errors.add("La date de l'ordonnance ne peut pas être dans le futur");
        }

        // Validation du dossier médical
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation de la consultation
        if (dto.getConsultationId() != null && dto.getConsultationId() <= 0) {
            errors.add("L'ID de la consultation doit être positif");
        }

        return errors;
    }

    public static List<String> validateDateRange(LocalDate startDate, LocalDate endDate) {
        List<String> errors = new ArrayList<>();

        if (startDate == null) {
            errors.add("La date de début est obligatoire");
        }

        if (endDate == null) {
            errors.add("La date de fin est obligatoire");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            errors.add("La date de début ne peut pas être après la date de fin");
        }

        return errors;
    }
}
