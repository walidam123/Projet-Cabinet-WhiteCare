package ma.whitecare.common.validators;

import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO;
import ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RDVValidator {

    private static final LocalTime HEURE_MIN = LocalTime.of(8, 0); // 8h00
    private static final LocalTime HEURE_MAX = LocalTime.of(19, 0); // 19h00

    public static List<String> validateCreateRDV(CreateRDVDTO dto, Consultation consultation) {
        List<String> errors = new ArrayList<>();

        // Validation de la consultation (obligatoire)
        if (dto.getConsultationId() == null) {
            errors.add("L'ID de la consultation est obligatoire");
        } else if (dto.getConsultationId() <= 0) {
            errors.add("L'ID de la consultation doit être positif");
        }

        if (consultation == null) {
            errors.add("La consultation spécifiée n'existe pas");
        }

        // Validation de la date
        if (dto.getDate() == null) {
            errors.add("La date du rendez-vous est obligatoire");
        } else {
            // La date ne peut pas être dans le passé (sauf si consultation déjà terminée)
            if (consultation != null && consultation.getDate() != null) {
                // Vérifier que la date du RDV correspond à la date de la consultation
                if (!dto.getDate().equals(consultation.getDate())) {
                    errors.add("La date du rendez-vous doit correspondre à la date de la consultation");
                }
            } else if (dto.getDate().isBefore(LocalDate.now())) {
                errors.add("La date du rendez-vous ne peut pas être dans le passé");
            }
        }

        // Validation de l'heure
        if (dto.getHeure() == null) {
            errors.add("L'heure du rendez-vous est obligatoire");
        } else {
            // Validation des heures de travail (8h-19h)
            if (dto.getHeure().isBefore(HEURE_MIN) || dto.getHeure().isAfter(HEURE_MAX)) {
                errors.add("L'heure du rendez-vous doit être entre 8h00 et 19h00");
            }
        }

        // Validation de la cohérence date/heure
        if (dto.getDate() != null && dto.getHeure() != null) {
            if (dto.getDate().equals(LocalDate.now()) && dto.getHeure().isBefore(LocalTime.now())) {
                errors.add("L'heure du rendez-vous ne peut pas être dans le passé pour aujourd'hui");
            }
        }

        // Validation du motif
        if (dto.getMotif() != null && dto.getMotif().length() > 100) {
            errors.add("Le motif ne peut pas dépasser 100 caractères");
        }

        // Validation de la note médecin
        if (dto.getNoteMedecin() != null && dto.getNoteMedecin().length() > 200) {
            errors.add("La note du médecin ne peut pas dépasser 200 caractères");
        }

        // Validation du dossier médical (si fourni)
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        // Validation du statut (par défaut PLANIFIE si non fourni)
        if (dto.getStatut() == null) {
            // Le statut sera défini par défaut à PLANIFIE
        }

        return errors;
    }

    public static List<String> validateUpdateRDV(UpdateRDVDTO dto, Consultation consultation) {
        List<String> errors = new ArrayList<>();

        // Validation de la consultation (si fournie)
        if (dto.getConsultationId() != null) {
            if (dto.getConsultationId() <= 0) {
                errors.add("L'ID de la consultation doit être positif");
            }
            if (consultation == null) {
                errors.add("La consultation spécifiée n'existe pas");
            }
        }

        // Validation de la date (si fournie)
        if (dto.getDate() != null) {
            if (consultation != null && consultation.getDate() != null) {
                // Vérifier que la date du RDV correspond à la date de la consultation
                if (!dto.getDate().equals(consultation.getDate())) {
                    errors.add("La date du rendez-vous doit correspondre à la date de la consultation");
                }
            } else if (dto.getDate().isBefore(LocalDate.now())) {
                errors.add("La date du rendez-vous ne peut pas être dans le passé");
            }
        }

        // Validation de l'heure (si fournie)
        if (dto.getHeure() != null) {
            // Validation des heures de travail (8h-19h)
            if (dto.getHeure().isBefore(HEURE_MIN) || dto.getHeure().isAfter(HEURE_MAX)) {
                errors.add("L'heure du rendez-vous doit être entre 8h00 et 19h00");
            }
        }

        // Validation de la cohérence date/heure
        if (dto.getDate() != null && dto.getHeure() != null) {
            if (dto.getDate().equals(LocalDate.now()) && dto.getHeure().isBefore(LocalTime.now())) {
                errors.add("L'heure du rendez-vous ne peut pas être dans le passé pour aujourd'hui");
            }
        }

        // Validation du motif (si fourni)
        if (dto.getMotif() != null && dto.getMotif().length() > 100) {
            errors.add("Le motif ne peut pas dépasser 100 caractères");
        }

        // Validation de la note médecin (si fournie)
        if (dto.getNoteMedecin() != null && dto.getNoteMedecin().length() > 200) {
            errors.add("La note du médecin ne peut pas dépasser 200 caractères");
        }

        // Validation du dossier médical (si fourni)
        if (dto.getDossierMedicaleId() != null && dto.getDossierMedicaleId() <= 0) {
            errors.add("L'ID du dossier médical doit être positif");
        }

        return errors;
    }

    /**
     * Valide qu'une transition de statut est autorisée
     * Règles logiques :
     * - PLANIFIE → CONFIRME, EN_SALLE, EN_CONSULTATION, TERMINE, ANNULE, ABSENT
     * - CONFIRME → EN_SALLE, EN_CONSULTATION, TERMINE, ANNULE, ABSENT
     * - EN_SALLE → EN_CONSULTATION, TERMINE, ANNULE, ABSENT
     * - EN_CONSULTATION → TERMINE, ANNULE, ABSENT
     * - TERMINE, ANNULE, ABSENT → aucune transition (statuts finaux)
     */
    public static List<String> validateStatusTransition(StatutRendezVous currentStatus, StatutRendezVous newStatus) {
        List<String> errors = new ArrayList<>();

        if (currentStatus == null || newStatus == null) {
            errors.add("Le statut actuel et le nouveau statut sont obligatoires");
            return errors;
        }

        // Si c'est le même statut, c'est valide
        if (currentStatus == newStatus) {
            return errors;
        }

        // Statuts finaux : aucune transition possible
        if (currentStatus == StatutRendezVous.TERMINE ||
            currentStatus == StatutRendezVous.ANNULE ||
            currentStatus == StatutRendezVous.ABSENT) {
            errors.add(String.format("Impossible de modifier un rendez-vous avec le statut %s", currentStatus));
            return errors;
        }

        // Transitions autorisées selon le statut actuel
        switch (currentStatus) {
            case PLANIFIE:
                // Peut passer à tous les autres statuts
                break;
            case CONFIRME:
                // Ne peut pas revenir à PLANIFIE
                if (newStatus == StatutRendezVous.PLANIFIE) {
                    errors.add("Impossible de revenir du statut CONFIRME à PLANIFIE");
                }
                break;
            case EN_SALLE:
                // Ne peut pas revenir à PLANIFIE ou CONFIRME
                if (newStatus == StatutRendezVous.PLANIFIE || newStatus == StatutRendezVous.CONFIRME) {
                    errors.add("Impossible de revenir du statut EN_SALLE à " + newStatus);
                }
                break;
            case EN_CONSULTATION:
                // Ne peut pas revenir à PLANIFIE, CONFIRME ou EN_SALLE
                if (newStatus == StatutRendezVous.PLANIFIE ||
                    newStatus == StatutRendezVous.CONFIRME ||
                    newStatus == StatutRendezVous.EN_SALLE) {
                    errors.add("Impossible de revenir du statut EN_CONSULTATION à " + newStatus);
                }
                break;
        }

        return errors;
    }

    /**
     * Vérifie si un RDV peut être modifié selon son statut
     */
    public static boolean canModifyRDV(StatutRendezVous statut) {
        if (statut == null) {
            return true;
        }
        // Les RDV avec statuts finaux ne peuvent pas être modifiés
        return statut != StatutRendezVous.TERMINE &&
               statut != StatutRendezVous.ANNULE &&
               statut != StatutRendezVous.ABSENT;
    }
}
