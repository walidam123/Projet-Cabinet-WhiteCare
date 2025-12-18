package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class DuplicateDossierMedicalException extends RuntimeException {
    public DuplicateDossierMedicalException(Long patientId) {
        super("Un dossier médical existe déjà pour ce patient (ID: " + patientId + ")");
    }
}
