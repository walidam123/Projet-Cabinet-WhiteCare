package ma.whitecare.common.exceptions;

public class MedicamentAlreadyExistsException extends RuntimeException {
    public MedicamentAlreadyExistsException(String field, String value) {
        super(String.format("Un médicament avec %s '%s' existe déjà", field, value));
    }

    public MedicamentAlreadyExistsException(String message) {
        super(message);
    }
}