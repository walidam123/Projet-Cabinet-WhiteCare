package ma.whitecare.service.modules.facture.api;

import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.enums.StatutFacture;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureService {

    // ========== CRUD FACTURES ==========
    Facture createFacture(Facture facture);
    Facture getFactureById(Long id);
    List<Facture> findAll();
    Facture updateFacture(Long id, Facture facture);
    void deleteFacture(Long id);

    // ========== RECHERCHES ==========
    boolean existsById(Long id);
    List<Facture> findBySituationFinanciereId(Long situationFinanciereId);
    List<Facture> findByConsultationId(Long consultationId);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Facture> findBySituationFinanciereIdAndStatut(Long situationFinanciereId, StatutFacture statut);

    // ========== STATISTIQUES ==========
    long countAll();
    long countByStatut(StatutFacture statut);
    long countBySituationFinanciereId(Long situationFinanciereId);

    // ========== GESTION DES STATUTS ==========
    void updateStatut(Long id, StatutFacture statut);

    // ========== GÉNÉRATION PDF ==========
    byte[] generatePDF(Long factureId) throws java.io.IOException;
}
