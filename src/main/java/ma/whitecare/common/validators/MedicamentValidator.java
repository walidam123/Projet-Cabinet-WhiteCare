package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.MedicamentDto.CreateMedicamentDTO;
import ma.whitecare.mvc.dto.MedicamentDto.UpdateMedicamentDTO;

import java.util.ArrayList;
import java.util.List;

public class MedicamentValidator {

    public static List<String> validateCreateMedicament(CreateMedicamentDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du nom
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            errors.add("Le nom du médicament est obligatoire");
        } else if (dto.getNom().length() < 2 || dto.getNom().length() > 25) {
            errors.add("Le nom du médicament doit contenir entre 2 et 25 caractères");
        }

        // Validation du laboratoire
        if (dto.getLaboratoire() == null || dto.getLaboratoire().trim().isEmpty()) {
            errors.add("Le laboratoire est obligatoire");
        } else if (dto.getLaboratoire().length() < 2 || dto.getLaboratoire().length() > 25) {
            errors.add("Le laboratoire doit contenir entre 2 et 25 caractères");
        }

        // Validation du type
        if (dto.getType() == null || dto.getType().trim().isEmpty()) {
            errors.add("Le type du médicament est obligatoire");
        } else if (dto.getType().length() < 2 || dto.getType().length() > 25) {
            errors.add("Le type doit contenir entre 2 et 50 caractères");
        }

        // Validation de la forme
        if (dto.getForme() == null) {
            errors.add("La forme du médicament est obligatoire");
        }

        // Validation du prix unitaire
        if (dto.getPrixUnitaire() == null) {
            errors.add("Le prix unitaire est obligatoire");
        } else if (dto.getPrixUnitaire() < 0) {
            errors.add("Le prix unitaire ne peut pas être négatif");
        } else if (dto.getPrixUnitaire() > 5000) {
            errors.add("Le prix unitaire ne peut pas dépasser 10000 DH");
        }

        // Validation de la description (optionnelle mais si présente, doit être valide)
        if (dto.getDescription() != null && dto.getDescription().length() > 100) {
            errors.add("La description ne peut pas dépasser 100 caractères");
        }

        return errors;
    }

    public static List<String> validateUpdateMedicament(UpdateMedicamentDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du nom (si fourni)
        if (dto.getNom() != null) {
            if (dto.getNom().trim().isEmpty()) {
                errors.add("Le nom du médicament ne peut pas être vide");
            } else if (dto.getNom().length() < 2 || dto.getNom().length() > 25) {
                errors.add("Le nom du médicament doit contenir entre 2 et 25 caractères");
            }
        }

        // Validation du laboratoire (si fourni)
        if (dto.getLaboratoire() != null) {
            if (dto.getLaboratoire().trim().isEmpty()) {
                errors.add("Le laboratoire ne peut pas être vide");
            } else if (dto.getLaboratoire().length() < 2 || dto.getLaboratoire().length() > 25) {
                errors.add("Le laboratoire doit contenir entre 2 et 25 caractères");
            }
        }

        // Validation du type (si fourni)
        if (dto.getType() != null) {
            if (dto.getType().trim().isEmpty()) {
                errors.add("Le type ne peut pas être vide");
            } else if (dto.getType().length() < 2 || dto.getType().length() > 25) {
                errors.add("Le type doit contenir entre 2 et 25 caractères");
            }
        }

        // Validation du prix unitaire (si fourni)
        if (dto.getPrixUnitaire() != null) {
            if (dto.getPrixUnitaire() < 0) {
                errors.add("Le prix unitaire ne peut pas être négatif");
            } else if (dto.getPrixUnitaire() > 5000) {
                errors.add("Le prix unitaire ne peut pas dépasser 5000 DH");
            }
        }

        // Validation de la description (si fournie)
        if (dto.getDescription() != null && dto.getDescription().length() > 100) {
            errors.add("La description ne peut pas dépasser 100 caractères");
        }

        return errors;
    }

    public static List<String> validateNom(String nom) {
        List<String> errors = new ArrayList<>();

        if (nom == null || nom.trim().isEmpty()) {
            errors.add("Le nom du médicament est obligatoire");
        } else if (nom.length() < 2 || nom.length() > 50) {
            errors.add("Le nom du médicament doit contenir entre 2 et 50 caractères");
        }

        return errors;
    }

    public static List<String> validatePrixUnitaire(Double prix) {
        List<String> errors = new ArrayList<>();

        if (prix == null) {
            errors.add("Le prix unitaire est obligatoire");
        } else if (prix < 0) {
            errors.add("Le prix unitaire ne peut pas être négatif");
        } else if (prix > 5000) {
            errors.add("Le prix unitaire ne peut pas dépasser 5000 DH");
        }

        return errors;
    }
}
