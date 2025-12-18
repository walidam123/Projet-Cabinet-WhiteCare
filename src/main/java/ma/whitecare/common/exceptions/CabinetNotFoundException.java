package ma.whitecare.common.exceptions;

public class CabinetNotFoundException extends RuntimeException {
    public CabinetNotFoundException(Long cabinetId) {
        super("Cabinet médical non trouvé avec l'ID: " + cabinetId);
    }

    public CabinetNotFoundException(String message) {
        super(message);
    }
}
