package ma.whitecare.common.validators;

import ma.whitecare.entities.financial.Revenues;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenuesValidator {

    public static List<String> validateRevenue(Revenues revenue) {
        List<String> errors = new ArrayList<>();

        if (revenue == null) {
            errors.add("Le revenu ne peut pas être nul");
            return errors;
        }

        // Validation du titre
        if (revenue.getTitre() == null || revenue.getTitre().trim().isEmpty()) {
            errors.add("Le titre est obligatoire");
        } else if (revenue.getTitre().length() < 3 || revenue.getTitre().length() > 100) {
            errors.add("Le titre doit contenir entre 3 et 100 caractères");
        }

        // Validation de la description (optionnelle mais si présente, doit être valide)
        if (revenue.getDescription() != null && !revenue.getDescription().trim().isEmpty()) {
            if (revenue.getDescription().length() > 255) {
                errors.add("La description ne peut pas dépasser 255 caractères");
            }
        }

        // Validation du montant
        if (revenue.getMontant() == null) {
            errors.add("Le montant est obligatoire");
        } else if (revenue.getMontant() <= 0) {
            errors.add("Le montant doit être supérieur à 0");
        } else if (revenue.getMontant() > 999999999.99) {
            errors.add("Le montant ne peut pas dépasser 999 999 999.99");
        }

        // Validation de la date
        if (revenue.getDate() == null) {
            errors.add("La date est obligatoire");
        } else if (revenue.getDate().isAfter(LocalDateTime.now())) {
            errors.add("La date ne peut pas être dans le futur");
        }

        // Validation du cabinet médical
        if (revenue.getCabinetMedicaleId() == null) {
            errors.add("L'ID du cabinet médical est obligatoire");
        } else if (revenue.getCabinetMedicaleId() <= 0) {
            errors.add("L'ID du cabinet médical doit être positif");
        }

        return errors;
    }

    public static List<String> validateRevenueForUpdate(Revenues revenue) {
        List<String> errors = validateRevenue(revenue);

        if (revenue != null && revenue.getId() == null) {
            errors.add("L'ID du revenu est obligatoire pour la mise à jour");
        }

        return errors;
    }

    public static void validateRevenueOrThrow(Revenues revenue) {
        List<String> errors = validateRevenue(revenue);
        if (!errors.isEmpty()) {
            throw new ma.whitecare.common.exceptions.InvalidRevenueException(errors);
        }
    }

    public static void validateRevenueForUpdateOrThrow(Revenues revenue) {
        List<String> errors = validateRevenueForUpdate(revenue);
        if (!errors.isEmpty()) {
            throw new ma.whitecare.common.exceptions.InvalidRevenueException(errors);
        }
    }
}

