package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class ConsultationNotFoundException extends RuntimeException {
    public ConsultationNotFoundException(Long consultationId) {
        super("Consultation non trouvée avec l'ID: " + consultationId);
    }
}
