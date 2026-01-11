package ma.whitecare.service.modules.rdv.api;

import ma.whitecare.entities.appointment.RDV;
import ma.whitecare.entities.enums.StatutRendezVous;
import ma.whitecare.mvc.dto.RDVDto.CreateRDVDTO;
import ma.whitecare.mvc.dto.RDVDto.RDVDTO;
import ma.whitecare.mvc.dto.RDVDto.UpdateRDVDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RDVService {

    // ========== CRUD RDV ==========
    RDV createRDV(CreateRDVDTO rdvDTO);
    RDV getRDVById(Long id);
    List<RDV> findAll();
    RDV updateRDV(Long id, UpdateRDVDTO updateDTO);
    void deleteRDV(Long id);

    // ========== RECHERCHES ==========
    boolean existsById(Long id);
    List<RDV> findByConsultationId(Long consultationId);
    List<RDV> findByDossierMedicaleId(Long dossierMedicaleId);
    List<RDV> findByStatut(StatutRendezVous statut);
    List<RDV> findByStatutAndDate(StatutRendezVous statut, LocalDate date);
    List<RDV> findByDate(LocalDate date);
    List<RDV> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<RDV> findByDossierMedicaleIdAndStatut(Long dossierMedicaleId, StatutRendezVous statut);
    List<RDV> findByConsultationIdAndStatut(Long consultationId, StatutRendezVous statut);

    // ========== GESTION DES STATUTS ==========
    RDV updateStatut(Long id, StatutRendezVous nouveauStatut);
    boolean canModifyRDV(Long id);

    // ========== STATISTIQUES ==========
    long countByStatut(StatutRendezVous statut);
    long countByDate(LocalDate date);

    // ========== VALIDATION ==========
    boolean existsByDateAndHeure(LocalDate date, LocalTime heure);
    boolean existsByConsultationId(Long consultationId);

    // ========== CONVERSIONS ==========
    RDVDTO convertToDTO(RDV rdv);
    List<RDVDTO> convertToDTOList(List<RDV> rdvs);
}
