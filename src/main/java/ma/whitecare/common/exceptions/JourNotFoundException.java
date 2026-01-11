package ma.whitecare.common.exceptions;

public class JourNotFoundException extends RuntimeException {
    public JourNotFoundException(Long jourId) {
        super("Jour non trouvé avec l'ID: " + jourId);
    }

    public JourNotFoundException(String message) {
        super(message);
    }
}
