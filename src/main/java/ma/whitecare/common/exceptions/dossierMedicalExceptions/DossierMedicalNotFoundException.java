package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class DossierMedicalNotFoundException extends RuntimeException {
    public DossierMedicalNotFoundException(Long dossierId) {
        super("Dossier médical non trouvé avec l'ID: " + dossierId);
    }
}
