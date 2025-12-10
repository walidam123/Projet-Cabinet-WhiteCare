package ma.whitecare.common.exceptions;



public class StaffAlreadyExistsException extends RuntimeException {

    public StaffAlreadyExistsException(String field, String value) {
        super("Staff avec " + field + " '" + value + "' existe déjà");
    }
}