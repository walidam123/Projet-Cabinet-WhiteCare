package ma.whitecare.common.exceptions;

public class PrescriptionNotFoundException extends RuntimeException {
    public PrescriptionNotFoundException(Long prescriptionId) {
        super("Prescription non trouvée avec l'ID: " + prescriptionId);
    }
}