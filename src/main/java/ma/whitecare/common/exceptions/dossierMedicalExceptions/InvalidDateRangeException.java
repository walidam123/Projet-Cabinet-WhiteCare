package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
