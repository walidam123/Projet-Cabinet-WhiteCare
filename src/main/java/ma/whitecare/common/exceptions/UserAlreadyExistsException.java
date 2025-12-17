package ma.whitecare.common.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String field, String value) {
        super("Un utilisateur avec le " + field + " '" + value + "' existe déjà");
    }
}