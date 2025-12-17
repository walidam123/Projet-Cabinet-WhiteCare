package ma.whitecare.common.exceptions;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException() {
        super("Permissions insuffisantes pour effectuer cette action");
    }
}