package ma.whitecare.common.exceptions;

public class MedecinNotFoundException extends RuntimeException {

    public MedecinNotFoundException(Long medecinId) {
        super("Médecin avec ID " + medecinId + " non trouvé");
    }

    public MedecinNotFoundException(String message) {
        super(message);
    }
}
