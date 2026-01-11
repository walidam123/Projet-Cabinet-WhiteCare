package ma.whitecare.common.validators;

import ma.whitecare.entities.agenda.Creneau;
import ma.whitecare.entities.agenda.Jour;
import ma.whitecare.entities.enums.JourSemaine;
import ma.whitecare.mvc.dto.CreneauDto.CreateCreneauDTO;
import ma.whitecare.mvc.dto.CreneauDto.UpdateCreneauDTO;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CreneauValidator {

    private static final LocalTime HEURE_MAX = LocalTime.of(19, 0); // 19h00
    private static final LocalTime HEURE_MAX_SAMEDI = LocalTime.of(12, 0); // 12h00 pour samedi

    public static List<String> validateCreateCreneau(CreateCreneauDTO dto, Jour jour) {
        List<String> errors = new ArrayList<>();

        // Validation du jour
        if (dto.getJourId() == null) {
            errors.add("L'ID du jour est obligatoire");
        } else if (dto.getJourId() <= 0) {
            errors.add("L'ID du jour doit être positif");
        }

        if (jour == null) {
            errors.add("Le jour spécifié n'existe pas");
        }

        // Validation de l'heure de début
        if (dto.getHeureDebut() == null) {
            errors.add("L'heure de début est obligatoire");
        }

        // Validation de l'heure de fin
        if (dto.getHeureFin() == null) {
            errors.add("L'heure de fin est obligatoire");
        }

        // Validation de la cohérence des heures
        if (dto.getHeureDebut() != null && dto.getHeureFin() != null) {
            if (!dto.getHeureFin().isAfter(dto.getHeureDebut())) {
                errors.add("L'heure de fin doit être après l'heure de début");
            }

            // Validation de la durée minimale (au moins 15 minutes)
            long dureeMinutes = java.time.Duration.between(dto.getHeureDebut(), dto.getHeureFin()).toMinutes();
            if (dureeMinutes < 15) {
                errors.add("La durée minimale d'un créneau est de 15 minutes");
            }

            // Validation de la durée maximale (maximum 4 heures)
            if (dureeMinutes > 240) {
                errors.add("La durée maximale d'un créneau est de 4 heures");
            }
        }

        // Validation selon le jour de la semaine
        if (jour != null && jour.getJourSemaine() != null) {
            errors.addAll(validateHeuresSelonJour(dto.getHeureDebut(), dto.getHeureFin(), jour.getJourSemaine()));
        }

        return errors;
    }

    public static List<String> validateUpdateCreneau(UpdateCreneauDTO dto, Jour jour) {
        List<String> errors = new ArrayList<>();

        // Validation de l'heure de début (si fournie)
        // Validation de l'heure de fin (si fournie)
        if (dto.getHeureDebut() != null && dto.getHeureFin() != null) {
            if (!dto.getHeureFin().isAfter(dto.getHeureDebut())) {
                errors.add("L'heure de fin doit être après l'heure de début");
            }

            // Validation de la durée minimale
            long dureeMinutes = java.time.Duration.between(dto.getHeureDebut(), dto.getHeureFin()).toMinutes();
            if (dureeMinutes < 15) {
                errors.add("La durée minimale d'un créneau est de 15 minutes");
            }

            // Validation de la durée maximale
            if (dureeMinutes > 240) {
                errors.add("La durée maximale d'un créneau est de 4 heures");
            }
        }

        // Validation selon le jour de la semaine si le jour est fourni
        if (jour != null && jour.getJourSemaine() != null) {
            LocalTime heureDebut = dto.getHeureDebut();
            LocalTime heureFin = dto.getHeureFin();
            if (heureDebut != null || heureFin != null) {
                errors.addAll(validateHeuresSelonJour(heureDebut, heureFin, jour.getJourSemaine()));
            }
        }

        return errors;
    }

    /**
     * Valide les heures selon le jour de la semaine
     * - Pas de RDV après 19h
     * - Pas de RDV le dimanche
     * - Samedi : arrêt à 12h
     */
    private static List<String> validateHeuresSelonJour(LocalTime heureDebut, LocalTime heureFin, JourSemaine jourSemaine) {
        List<String> errors = new ArrayList<>();

        if (jourSemaine == JourSemaine.DIMANCHE) {
            errors.add("Les rendez-vous ne sont pas autorisés le dimanche");
            return errors; // Pas besoin de vérifier les heures si c'est dimanche
        }

        if (heureDebut != null && heureFin != null) {
            // Validation pour samedi : arrêt à 12h
            if (jourSemaine == JourSemaine.SAMEDI) {
                if (heureDebut.isAfter(HEURE_MAX_SAMEDI) || heureFin.isAfter(HEURE_MAX_SAMEDI)) {
                    errors.add("Le samedi, les rendez-vous doivent se terminer avant 12h00");
                }
            }

            // Validation générale : pas après 19h
            if (heureDebut.isAfter(HEURE_MAX) || heureFin.isAfter(HEURE_MAX)) {
                errors.add("Les rendez-vous ne sont pas autorisés après 19h00");
            }
        }

        return errors;
    }

    /**
     * Vérifie si deux créneaux se chevauchent
     */
    public static boolean creneauxSeChevauchent(Creneau creneau1, Creneau creneau2) {
        if (creneau1 == null || creneau2 == null) {
            return false;
        }
        return creneau1.chevauche(creneau2);
    }

    /**
     * Valide qu'un créneau ne chevauche pas avec d'autres créneaux existants
     */
    public static List<String> validatePasDeChevauchement(Creneau nouveauCreneau, List<Creneau> creneauxExistants) {
        List<String> errors = new ArrayList<>();

        if (nouveauCreneau == null || creneauxExistants == null) {
            return errors;
        }

        for (Creneau creneauExistant : creneauxExistants) {
            // Ignorer si le créneau existant est le même (pour les mises à jour)
            if (creneauExistant.getId() != null && nouveauCreneau.getId() != null &&
                    creneauExistant.getId().equals(nouveauCreneau.getId())) {
                continue;
            }

            // Vérifier le chevauchement seulement si les deux créneaux sont disponibles ou ont un rendez-vous
            if (creneauExistant.isEstDisponible() || creneauExistant.getRendezVousId() != null) {
                if (creneauxSeChevauchent(nouveauCreneau, creneauExistant)) {
                    errors.add(String.format(
                            "Le créneau chevauche avec un autre créneau existant (%s - %s)",
                            creneauExistant.getHeureDebut(),
                            creneauExistant.getHeureFin()
                    ));
                    break; // Un seul chevauchement suffit pour invalider
                }
            }
        }

        return errors;
    }
}
