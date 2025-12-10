package ma.whitecare.common.exceptions;

public class CabinetNotFoundException extends RuntimeException {

    public CabinetNotFoundException(Long cabinetId) {
        super("Cabinet médical avec ID " + cabinetId + " non trouvé");
    }
}
