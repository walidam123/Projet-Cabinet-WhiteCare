package ma.whitecare.repository.modules.facture;

import ma.whitecare.entities.financial.Facture;
import ma.whitecare.entities.enums.StatutFacture;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureRepository extends CrudRepository<Facture,Long> {
    List<Facture> findBySituationFinanciereId(Long situationFinanciereId);
    List<Facture> findByConsultationId(Long consultationId);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Facture> findBySituationFinanciereIdAndStatut(Long situationFinanciereId, StatutFacture statut);
    boolean existsById(Long factureId);
    long countAll();
    long countByStatut(StatutFacture statut);
    long countBySituationFinanciereId(Long situationFinanciereId);
    void updateStatut(Long id, StatutFacture statut);
}
