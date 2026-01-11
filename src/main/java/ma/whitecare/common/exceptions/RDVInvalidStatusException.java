package ma.whitecare.common.exceptions;

import ma.whitecare.entities.enums.StatutRendezVous;

public class RDVInvalidStatusException extends RuntimeException {
    public RDVInvalidStatusException(String message) {
        super(message);
    }

    public RDVInvalidStatusException(StatutRendezVous currentStatus, StatutRendezVous attemptedStatus) {
        super(String.format("Transition de statut invalide : impossible de passer de %s à %s",
                currentStatus, attemptedStatus));
    }
}
