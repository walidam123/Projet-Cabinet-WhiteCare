package ma.whitecare.common.exceptions;


public class StaffNotFoundException extends RuntimeException {

    public StaffNotFoundException(Long staffId) {
        super("Staff avec ID " + staffId + " non trouvé");
    }

    public StaffNotFoundException(String field, String value) {
        super("Staff avec " + field + " '" + value + "' non trouvé");
    }
}
