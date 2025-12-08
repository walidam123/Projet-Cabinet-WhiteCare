package ma.whitecare.common.exceptions;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Le compte est verrouillé");
    }
}