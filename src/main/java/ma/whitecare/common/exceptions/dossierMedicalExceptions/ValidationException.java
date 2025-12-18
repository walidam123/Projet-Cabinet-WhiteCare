package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String fieldName, String reason) {
        super(fieldName + " : " + reason);
    }
}
