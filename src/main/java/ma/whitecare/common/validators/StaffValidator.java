package ma.whitecare.common.validators;



import ma.whitecare.mvc.dto.UpdateStaffDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffValidator {

    public static List<String> validateUpdateStaff(UpdateStaffDTO updateDTO) {
        List<String> errors = new ArrayList<>();

        // Validation salaire
        if (updateDTO.getSalaire() != null && updateDTO.getSalaire() < 0) {
            errors.add("Le salaire doit être un nombre positif");
        }

        // Validation prime
        if (updateDTO.getPrime() != null && updateDTO.getPrime() < 0) {
            errors.add("La prime doit être un nombre positif");
        }

        // Validation solde congé
        if (updateDTO.getSoldeConge() != null && updateDTO.getSoldeConge() < 0) {
            errors.add("Le solde de congé doit être un nombre positif");
        }



        return errors;
    }

    public static List<String> validateSalaire(Double salaire) {
        List<String> errors = new ArrayList<>();

        if (salaire == null) {
            errors.add("Le salaire est obligatoire");
        } else if (salaire < 0) {
            errors.add("Le salaire doit être un nombre positif");
        } else if (salaire < 3000) { // SMIC Maroc 2024
            errors.add("Le salaire doit être au moins égal au SMIC (3000 DH)");
        }

        return errors;
    }

    public static List<String> validatePrime(Double prime) {
        List<String> errors = new ArrayList<>();

        if (prime != null && prime < 0) {
            errors.add("La prime doit être un nombre positif");
        }

        return errors;
    }

    public static List<String> validateSoldeConge(Integer soldeConge) {
        List<String> errors = new ArrayList<>();

        if (soldeConge == null) {
            errors.add("Le solde de congé est obligatoire");
        } else if (soldeConge < 0) {
            errors.add("Le solde de congé doit être un nombre positif");
        } else if (soldeConge > 60) {
            errors.add("Le solde de congé ne peut pas dépasser 60 jours");
        }

        return errors;
    }

    public static List<String> validateDateRecrutement(LocalDate dateRecrutement) {
        List<String> errors = new ArrayList<>();

        if (dateRecrutement != null && dateRecrutement.isAfter(LocalDate.now())) {
            errors.add("La date de recrutement ne peut pas être dans le futur");
        }

        return errors;
    }
}