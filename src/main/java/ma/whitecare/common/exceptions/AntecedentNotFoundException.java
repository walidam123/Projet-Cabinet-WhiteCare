package ma.whitecare.common.exceptions;

public class AntecedentNotFoundException extends RuntimeException {
    public AntecedentNotFoundException(Long id) {
        super("Antécédent avec ID " + id + " non trouvé.");
    }
}
