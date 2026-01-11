package ma.whitecare.common.exceptions;

public class CreneauNotFoundException extends RuntimeException {
    public CreneauNotFoundException(Long creneauId) {
        super("Créneau non trouvé avec l'ID: " + creneauId);
    }

    public CreneauNotFoundException(String message) {
        super(message);
    }
}
