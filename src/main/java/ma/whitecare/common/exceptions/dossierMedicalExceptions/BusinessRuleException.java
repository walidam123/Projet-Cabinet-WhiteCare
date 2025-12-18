package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
