package ma.whitecare.repository.modules.dossierMedical.api;

import ma.whitecare.entities.financial.SituationFinanciere;
import ma.whitecare.entities.enums.EnPromo;
import ma.whitecare.entities.enums.StatutSituationFinanciere;
import ma.whitecare.repository.common.CrudRepository;

import java.util.List;

public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere,Long> {
    SituationFinanciere findByDossierMedicaleId(Long dossierMedicaleId);
    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);
    List<SituationFinanciere> findByEnPromo(EnPromo enPromo);
    List<SituationFinanciere> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutSituationFinanciere statut);
    boolean existsById(Long situationFinanciereId);
    boolean existsByDossierMedicaleId(Long dossierMedicaleId);
    long countAll();
    long countByStatut(StatutSituationFinanciere statut);
    long countByDossierMedicaleId(Long dossierMedicaleId);
    void updateStatut(Long id, StatutSituationFinanciere statut);
}
