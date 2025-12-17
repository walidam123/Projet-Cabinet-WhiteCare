package ma.whitecare.common.validators;




import ma.whitecare.mvc.dto.CreateUserDTO;
import ma.whitecare.mvc.dto.UpdateUserDTO;
import ma.whitecare.mvc.dto.UpdateProfileDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserValidator {

    public static List<String> validateCreateUser(CreateUserDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            errors.add("Le nom est obligatoire");
        } else if (dto.getNom().length() < 2 || dto.getNom().length() > 50) {
            errors.add("Le nom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            errors.add("Le prénom est obligatoire");
        } else if (dto.getPrenom().length() < 2 || dto.getPrenom().length() > 50) {
            errors.add("Le prénom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
            errors.add("Le login est obligatoire");
        } else if (dto.getLogin().length() < 3 || dto.getLogin().length() > 30) {
            errors.add("Le login doit contenir entre 3 et 30 caractères");
        } else if (!dto.getLogin().matches("^[a-zA-Z0-9_]+$")) {
            errors.add("Le login ne peut contenir que des lettres, chiffres et underscores");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            errors.add("Le mot de passe est obligatoire");
        } else if (dto.getPassword().length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caractères");
        } else {
            errors.addAll(validatePassword(dto.getPassword()));
        }

        if (dto.getCin() == null || dto.getCin().trim().isEmpty()) {
            errors.add("Le CIN est obligatoire");
        } else if (dto.getCin().length() < 6 || dto.getCin().length() > 20) {
            errors.add("Le CIN doit contenir entre 6 et 20 caractères");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("L'email est obligatoire");
        } else if (!isValidEmail(dto.getEmail())) {
            errors.add("L'email n'est pas valide");
        }

        if (dto.getTelephone() != null && !dto.getTelephone().trim().isEmpty()) {
            if (!dto.getTelephone().matches("^\\+?[0-9\\s\\-]{8,}$")) {
                errors.add("Le téléphone n'est pas valide");
            }
        }

        if (dto.getDateNaissance() != null && dto.getDateNaissance().isAfter(LocalDate.now())) {
            errors.add("La date de naissance ne peut pas être dans le futur");
        }

        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            errors.add("Au moins un rôle doit être spécifié");
        }

        return errors;
    }

    public static List<String> validateUpdateUser(UpdateUserDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            errors.add("Le nom est obligatoire");
        } else if (dto.getNom().length() < 2 || dto.getNom().length() > 50) {
            errors.add("Le nom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            errors.add("Le prénom est obligatoire");
        } else if (dto.getPrenom().length() < 2 || dto.getPrenom().length() > 50) {
            errors.add("Le prénom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("L'email est obligatoire");
        } else if (!isValidEmail(dto.getEmail())) {
            errors.add("L'email n'est pas valide");
        }

        if (dto.getDateNaissance() != null && dto.getDateNaissance().isAfter(LocalDate.now())) {
            errors.add("La date de naissance ne peut pas être dans le futur");
        }

        return errors;
    }

    public static List<String> validateUpdateProfile(UpdateProfileDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            errors.add("Le nom est obligatoire");
        } else if (dto.getNom().length() < 2 || dto.getNom().length() > 50) {
            errors.add("Le nom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            errors.add("Le prénom est obligatoire");
        } else if (dto.getPrenom().length() < 2 || dto.getPrenom().length() > 50) {
            errors.add("Le prénom doit contenir entre 2 et 50 caractères");
        }

        if (dto.getTelephone() != null && !dto.getTelephone().trim().isEmpty()) {
            if (!dto.getTelephone().matches("^\\+?[0-9\\s\\-]{8,}$")) {
                errors.add("Le téléphone n'est pas valide");
            }
        }

        if (dto.getDateNaissance() != null && dto.getDateNaissance().isAfter(LocalDate.now())) {
            errors.add("La date de naissance ne peut pas être dans le futur");
        }

        return errors;
    }

    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password.length() < 8) {
            errors.add("Le mot de passe doit contenir au moins 8 caractères");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Le mot de passe doit contenir au moins une majuscule");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.add("Le mot de passe doit contenir au moins une minuscule");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("Le mot de passe doit contenir au moins un chiffre");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            errors.add("Le mot de passe doit contenir au moins un caractère spécial");
        }

        return errors;
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}