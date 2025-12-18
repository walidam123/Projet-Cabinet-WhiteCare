package ma.whitecare.common.exceptions;

public class RevenuesNotFoundException extends RuntimeException {
    public RevenuesNotFoundException(Long revenueId) {
        super("Revenu non trouvé avec l'ID: " + revenueId);
    }

    public RevenuesNotFoundException(String message) {
        super(message);
    }
}

