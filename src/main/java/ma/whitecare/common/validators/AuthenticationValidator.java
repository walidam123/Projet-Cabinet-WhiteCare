package ma.whitecare.common.validators;

import ma.whitecare.mvc.dto.AuthDto.LoginRequestDto;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationValidator {

    public static List<String> validateLoginRequest(LoginRequestDto request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("La requête de connexion ne peut pas être nulle");
            return errors;
        }

        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            errors.add("Le login est obligatoire");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.add("Le mot de passe est obligatoire");
        }

        return errors;
    }
}
