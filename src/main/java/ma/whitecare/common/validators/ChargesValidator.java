package ma.whitecare.common.validators;

import ma.whitecare.entities.financial.Charges;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesValidator {

    public static List<String> validateCharge(Charges charge) {
        List<String> errors = new ArrayList<>();

        if (charge == null) {
            errors.add("La charge ne peut pas être nulle");
            return errors;
        }

        // Validation du titre
        if (charge.getTitre() == null || charge.getTitre().trim().isEmpty()) {
            errors.add("Le titre est obligatoire");
        } else if (charge.getTitre().length() < 3 || charge.getTitre().length() > 100) {
            errors.add("Le titre doit contenir entre 3 et 100 caractères");
        }

        // Validation de la description (optionnelle mais si présente, doit être valide)
        if (charge.getDescription() != null && !charge.getDescription().trim().isEmpty()) {
            if (charge.getDescription().length() > 255) {
                errors.add("La description ne peut pas dépasser 255 caractères");
            }
        }

        // Validation du montant
        if (charge.getMontant() == null) {
            errors.add("Le montant est obligatoire");
        } else if (charge.getMontant() <= 0) {
            errors.add("Le montant doit être supérieur à 0");
        } else if (charge.getMontant() > 999999999.99) {
            errors.add("Le montant ne peut pas dépasser 999 999 999.99");
        }

        // Validation de la date
        if (charge.getDate() == null) {
            errors.add("La date est obligatoire");
        } else if (charge.getDate().isAfter(LocalDateTime.now())) {
            errors.add("La date ne peut pas être dans le futur");
        }

        // Validation du cabinet médical
        if (charge.getCabinet().getId() == null) {
            errors.add("L'ID du cabinet médical est obligatoire");
        } else if (charge.getCabinet().getId() <= 0) {
            errors.add("L'ID du cabinet médical doit être positif");
        }

        return errors;
    }

    public static List<String> validateChargeForUpdate(Charges charge) {
        List<String> errors = validateCharge(charge);

        if (charge != null && charge.getId() == null) {
            errors.add("L'ID de la charge est obligatoire pour la mise à jour");
        }

        return errors;
    }

    public static void validateChargeOrThrow(Charges charge) {
        List<String> errors = validateCharge(charge);
        if (!errors.isEmpty()) {
            throw new ma.whitecare.common.exceptions.InvalidChargeException(errors);
        }
    }

    public static void validateChargeForUpdateOrThrow(Charges charge) {
        List<String> errors = validateChargeForUpdate(charge);
        if (!errors.isEmpty()) {
            throw new ma.whitecare.common.exceptions.InvalidChargeException(errors);
        }
    }
}

