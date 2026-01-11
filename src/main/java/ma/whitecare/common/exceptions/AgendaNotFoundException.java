package ma.whitecare.common.exceptions;

public class AgendaNotFoundException extends RuntimeException {
    public AgendaNotFoundException(Long agendaId) {
        super("Agenda non trouvé avec l'ID: " + agendaId);
    }

    public AgendaNotFoundException(String message) {
        super(message);
    }
}
