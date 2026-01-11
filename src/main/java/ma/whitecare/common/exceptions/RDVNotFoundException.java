package ma.whitecare.common.exceptions;

public class RDVNotFoundException extends RuntimeException {
    public RDVNotFoundException(Long rdvId) {
        super("Rendez-vous non trouvé avec l'ID: " + rdvId);
    }

    public RDVNotFoundException(String message) {
        super(message);
    }
}
