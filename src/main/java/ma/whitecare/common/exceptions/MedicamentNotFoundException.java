package ma.whitecare.common.exceptions;

public class MedicamentNotFoundException extends RuntimeException {
    public MedicamentNotFoundException(Long medicamentId) {
        super("Médicament non trouvé avec l'ID: " + medicamentId);
    }

    public MedicamentNotFoundException(String message) {
        super(message);
    }
}