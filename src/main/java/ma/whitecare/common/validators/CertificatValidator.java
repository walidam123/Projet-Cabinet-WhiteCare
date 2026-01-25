package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.CertificatDto.CreateCertificatDTO;
import ma.whitecare.mvc.dto.CertificatDto.UpdateCertificatDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificatValidator {

    public static List<String> validateCreateCertificat(CreateCertificatDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation de la date de début
        if (dto.getDateDebut() == null) {
            errors.add("La date de début est obligatoire");
        }

        // Validation de la date de fin
        if (dto.getDateFin() == null) {
            errors.add("La date de fin est obligatoire");
        }

        // Validation de la cohérence des dates
        if (dto.getDateDebut() != null && dto.getDateFin() != null) {
            if (dto.getDateFin().isBefore(dto.getDateDebut())) {
                errors.add("La date de fin ne peut pas être antérieure à la date de début");
            }
        }

        // Validation de la durée
        if (dto.getDuree() != null) {
            if (dto.getDuree() < 1) {
                errors.add("La durée doit être d'au moins 1 jour");
            } else if (dto.getDuree() > 365) {
                errors.add("La durée ne peut pas dépasser 365 jours");
            }

            // Vérifier la cohérence avec les dates
            if (dto.getDateDebut() != null && dto.getDateFin() != null) {
                long calculatedDays = java.time.temporal.ChronoUnit.DAYS.between(
                        dto.getDateDebut(), dto.getDateFin()) + 1;
                if (dto.getDuree() != calculatedDays) {
                    errors.add("La durée doit correspondre à la différence entre la date de fin et la date de début");
                }
            }
        } else {
            // Calculer automatiquement la durée si non fournie
            if (dto.getDateDebut() != null && dto.getDateFin() != null) {
                long calculatedDays = java.time.temporal.ChronoUnit.DAYS.between(
                        dto.getDateDebut(), dto.getDateFin()) + 1;
                if (calculatedDays < 1) {
                    errors.add("La durée calculée doit être d'au moins 1 jour");
                }
            }
        }

        // Validation de la note médecin
        if (dto.getNoteMedecin() != null && dto.getNoteMedecin().length() > 500) {
            errors.add("La note du médecin ne peut pas dépasser 500 caractères");
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

    public static List<String> validateUpdateCertificat(UpdateCertificatDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation de la date de début (si fournie)
        if (dto.getDateDebut() != null) {
            if (dto.getDateDebut().isAfter(LocalDate.now())) {
                errors.add("La date de début ne peut pas être dans le futur");
            }
        }

        // Validation de la date de fin (si fournie)
        if (dto.getDateFin() != null) {
            if (dto.getDateFin().isAfter(LocalDate.now())) {
                errors.add("La date de fin ne peut pas être dans le futur");
            }
        }

        // Validation de la cohérence des dates
        if (dto.getDateDebut() != null && dto.getDateFin() != null) {
            if (dto.getDateFin().isBefore(dto.getDateDebut())) {
                errors.add("La date de fin ne peut pas être antérieure à la date de début");
            }
        }

        // Validation de la durée (si fournie)
        if (dto.getDuree() != null) {
            if (dto.getDuree() < 1) {
                errors.add("La durée doit être d'au moins 1 jour");
            } else if (dto.getDuree() > 365) {
                errors.add("La durée ne peut pas dépasser 365 jours");
            }

            // Vérifier la cohérence avec les dates si toutes les deux sont fournies
            if (dto.getDateDebut() != null && dto.getDateFin() != null) {
                long calculatedDays = java.time.temporal.ChronoUnit.DAYS.between(
                        dto.getDateDebut(), dto.getDateFin()) + 1;
                if (dto.getDuree() != calculatedDays) {
                    errors.add("La durée doit correspondre à la différence entre la date de fin et la date de début");
                }
            }
        }

        // Validation de la note médecin (si fournie)
        if (dto.getNoteMedecin() != null && dto.getNoteMedecin().length() > 500) {
            errors.add("La note du médecin ne peut pas dépasser 500 caractères");
        }

        // Validation du dossier médical (si fourni)
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation de la consultation (si fournie)
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

        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                errors.add("La date de fin ne peut pas être antérieure à la date de début");
            }
        }

        return errors;
    }
}
