package ma.whitecare.common.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Mot de passe invalide");
    }
}

