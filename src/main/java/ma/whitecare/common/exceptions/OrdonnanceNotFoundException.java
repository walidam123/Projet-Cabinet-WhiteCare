package ma.whitecare.common.exceptions;

public class OrdonnanceNotFoundException extends RuntimeException {
    public OrdonnanceNotFoundException(Long ordonnanceId) {
        super("Ordonnance non trouvée avec l'ID: " + ordonnanceId);
    }

    public OrdonnanceNotFoundException(String message) {
        super(message);
    }
}
