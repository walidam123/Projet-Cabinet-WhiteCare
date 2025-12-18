package ma.whitecare.common.exceptions.dossierMedicalExceptions;

public class RelatedEntityNotFoundException extends RuntimeException {
    public RelatedEntityNotFoundException(String entityName, Long entityId) {
        super("Le " + entityName + " avec l'ID " + entityId + " n'existe pas");
    }
    
    public RelatedEntityNotFoundException(String entityName, String identifier) {
        super("Le " + entityName + " avec l'identifiant '" + identifier + "' n'existe pas");
    }
}
