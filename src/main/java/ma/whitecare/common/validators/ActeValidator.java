package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.ActeDto.CreateActeDTO;
import ma.whitecare.mvc.dto.ActeDto.UpdateActeDTO;

import java.util.ArrayList;
import java.util.List;

public class ActeValidator {

    public static List<String> validateCreateActe(CreateActeDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du libellé
        if (dto.getLibelle() == null || dto.getLibelle().trim().isEmpty()) {
            errors.add("Le libellé de l'acte est obligatoire");
        } else if (dto.getLibelle().length() < 2 || dto.getLibelle().length() > 100) {
            errors.add("Le libellé doit contenir entre 2 et 100 caractères");
        }

        // Validation de la catégorie
        if (dto.getCategorie() == null || dto.getCategorie().trim().isEmpty()) {
            errors.add("La catégorie de l'acte est obligatoire");
        } else if (dto.getCategorie().length() < 2 || dto.getCategorie().length() > 50) {
            errors.add("La catégorie doit contenir entre 2 et 50 caractères");
        }

        // Validation du prix de base
        if (dto.getPrixDeBase() == null) {
            errors.add("Le prix de base est obligatoire");
        } else if (dto.getPrixDeBase() < 0) {
            errors.add("Le prix de base ne peut pas être négatif");
        } else if (dto.getPrixDeBase() > 100000) {
            errors.add("Le prix de base ne peut pas dépasser 100000 DH");
        }

        return errors;
    }

    public static List<String> validateUpdateActe(UpdateActeDTO dto) {
        List<String> errors = new ArrayList<>();

        // Validation du libellé (si fourni)
        if (dto.getLibelle() != null) {
            if (dto.getLibelle().trim().isEmpty()) {
                errors.add("Le libellé ne peut pas être vide");
            } else if (dto.getLibelle().length() < 2 || dto.getLibelle().length() > 100) {
                errors.add("Le libellé doit contenir entre 2 et 100 caractères");
            }
        }

        // Validation de la catégorie (si fournie)
        if (dto.getCategorie() != null) {
            if (dto.getCategorie().trim().isEmpty()) {
                errors.add("La catégorie ne peut pas être vide");
            } else if (dto.getCategorie().length() < 2 || dto.getCategorie().length() > 50) {
                errors.add("La catégorie doit contenir entre 2 et 50 caractères");
            }
        }

        // Validation du prix de base (si fourni)
        if (dto.getPrixDeBase() != null) {
            if (dto.getPrixDeBase() < 0) {
                errors.add("Le prix de base ne peut pas être négatif");
            } else if (dto.getPrixDeBase() > 100000) {
                errors.add("Le prix de base ne peut pas dépasser 100000 DH");
            }
        }

        return errors;
    }

    public static List<String> validateLibelle(String libelle) {
        List<String> errors = new ArrayList<>();

        if (libelle == null || libelle.trim().isEmpty()) {
            errors.add("Le libellé de l'acte est obligatoire");
        } else if (libelle.length() < 2 || libelle.length() > 100) {
            errors.add("Le libellé doit contenir entre 2 et 100 caractères");
        }

        return errors;
    }

    public static List<String> validatePrixDeBase(Double prix) {
        List<String> errors = new ArrayList<>();

        if (prix == null) {
            errors.add("Le prix de base est obligatoire");
        } else if (prix < 0) {
            errors.add("Le prix de base ne peut pas être négatif");
        } else if (prix > 100000) {
            errors.add("Le prix de base ne peut pas dépasser 100000 DH");
        }

        return errors;
    }
}
