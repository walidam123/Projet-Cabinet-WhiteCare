package ma.whitecare.common.exceptions;

public class ConsultationNotFoundException extends RuntimeException {
    public ConsultationNotFoundException(Long consultationId) {
        super("Consultation non trouvée avec l'ID: " + consultationId);
    }

    public ConsultationNotFoundException(String message) {
        super(message);
    }
}