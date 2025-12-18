package ma.whitecare.common.exceptions;

import java.util.List;

public class InvalidChargeException extends RuntimeException {
    private final List<String> validationErrors;

    public InvalidChargeException(List<String> validationErrors) {
        super("Données de charge invalides: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }

    public InvalidChargeException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}

