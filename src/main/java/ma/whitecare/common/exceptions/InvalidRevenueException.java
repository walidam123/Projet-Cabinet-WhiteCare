package ma.whitecare.common.exceptions;

import java.util.List;

public class InvalidRevenueException extends RuntimeException {
    private final List<String> validationErrors;

    public InvalidRevenueException(List<String> validationErrors) {
        super("Données de revenu invalides: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }

    public InvalidRevenueException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}

