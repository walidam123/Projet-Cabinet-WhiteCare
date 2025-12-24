package ma.whitecare.common.exceptions;

public class ActeAlreadyExistsException extends RuntimeException {
    public ActeAlreadyExistsException(String field, String value) {
        super(String.format("Un acte avec %s '%s' existe déjà", field, value));
    }

    public ActeAlreadyExistsException(String message) {
        super(message);
    }
}
