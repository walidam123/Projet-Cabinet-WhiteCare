package ma.whitecare.common.exceptions;

public class ActeNotFoundException extends RuntimeException {
    public ActeNotFoundException(Long acteId) {
        super("Acte non trouvé avec l'ID: " + acteId);
    }

    public ActeNotFoundException(String message) {
        super(message);
    }
}
