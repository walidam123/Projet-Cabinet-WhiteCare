package ma.whitecare.common.exceptions;

public class DossierMedicalNotFoundException extends RuntimeException {
    public DossierMedicalNotFoundException(Long dossierId) {
        super("Dossier médical non trouvé avec l'ID: " + dossierId);
    }

    public DossierMedicalNotFoundException(String message) {
        super(message);
    }
}