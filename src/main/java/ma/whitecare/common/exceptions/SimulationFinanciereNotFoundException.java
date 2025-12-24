package ma.whitecare.common.exceptions;

public class SimulationFinanciereNotFoundException extends RuntimeException {
    public SimulationFinanciereNotFoundException(Long simulationId) {
        super("Simulation financière non trouvée avec l'ID: " + simulationId);
    }

    public SimulationFinanciereNotFoundException(String message) {
        super(message);
    }
}
