package ma.whitecare.common.exceptions;

public class SecretaireNotFoundException extends RuntimeException {

    public SecretaireNotFoundException(Long secretaireId) {
        super("Secrétaire avec ID " + secretaireId + " non trouvé");
    }

    public SecretaireNotFoundException(String message) {
        super(message);
    }
}
