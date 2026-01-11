package ma.whitecare.repository.modules.rdv.api;

import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RDVRepository extends CrudRepository<RDV, Long> {

    // ========== RECHERCHES PAR CONSULTATION ==========
    List<RDV> findByConsultationId(Long consultationId);
    Optional<RDV> findFirstByConsultationId(Long consultationId);

    // ========== RECHERCHES PAR DOSSIER MÉDICAL ==========
    List<RDV> findByDossierMedicaleId(Long dossierMedicaleId);

    // ========== RECHERCHES PAR STATUT ==========
    List<RDV> findByStatut(StatutRendezVous statut);
    List<RDV> findByStatutAndDate(StatutRendezVous statut, LocalDate date);

    // ========== RECHERCHES PAR DATE ==========
    List<RDV> findByDate(LocalDate date);
    List<RDV> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<RDV> findByDateAndHeure(LocalDate date, LocalTime heure);

    // ========== RECHERCHES COMBINÉES ==========
    List<RDV> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutRendezVous statut);
    List<RDV> findByConsultationIdAndStatut(Long consultationId, StatutRendezVous statut);

    // ========== VÉRIFICATIONS ==========
    boolean existsById(Long id);
    boolean existsByConsultationId(Long consultationId);
    boolean existsByDateAndHeure(LocalDate date, LocalTime heure);
    long countByStatut(StatutRendezVous statut);
    long countByDate(LocalDate date);
}
