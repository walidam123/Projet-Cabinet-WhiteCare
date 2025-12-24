package ma.whitecare.common.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Patient avec ID " + id + " non trouvé.");
    }
}