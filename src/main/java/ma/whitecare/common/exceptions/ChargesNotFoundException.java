package ma.whitecare.common.exceptions;

public class ChargesNotFoundException extends RuntimeException {
    public ChargesNotFoundException(Long chargeId) {
        super("Charge non trouvée avec l'ID: " + chargeId);
    }

    public ChargesNotFoundException(String message) {
        super(message);
    }
}

