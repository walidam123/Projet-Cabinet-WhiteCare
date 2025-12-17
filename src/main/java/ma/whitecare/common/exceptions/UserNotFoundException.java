package ma.whitecare.common.exceptions;



public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Utilisateur non trouvé avec l'ID: " + userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}






