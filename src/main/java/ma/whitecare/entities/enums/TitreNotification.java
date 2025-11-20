package ma.whitecare.entities.enums;

public enum TitreNotification {
    RAPPEL_RDV_DEMAIN("Rappel: Rendez-vous demain"),
    RAPPEL_RDV_AUJOURDHUI("Rappel: Rendez-vous aujourd'hui"),
    FACTURE_EN_RETARD("Facture en retard de paiement"),
    URGENCE_DISPONIBLE("Urgence médicale à traiter"),
    NOUVEL_UTILISATEUR("Nouvel utilisateur créé"),
    CONTRÔLE_SEMESTRIEL("Rappel: Contrôle semestriel"),
    ORDONNANCE_RENOUVELER("Ordonnance à renouveler"),
    STOCK_FAIBLE("Stock de médicaments faible"),
    SAUVEGARDE_REUSSIE("Sauvegarde automatique réussie"),
    ERREUR_SYSTEME("Erreur système détectée");

    private final String libelle;

    TitreNotification(String libelle) {
        this.libelle = libelle;
    }
    public String getLibelle() {
        return libelle;
    }
}
