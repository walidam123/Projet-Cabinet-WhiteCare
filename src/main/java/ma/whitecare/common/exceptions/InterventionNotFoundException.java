package ma.whitecare.common.exceptions;

public class InterventionNotFoundException extends RuntimeException {
    public InterventionNotFoundException(Long interventionId) {
        super("Intervention non trouvée avec l'ID: " + interventionId);
    }
}