package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.medical.Consultation;
import ma.whitecare.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {

    // ========== CRUD CONSULTATION ==========
    Consultation createConsultation(Consultation consultation);
    Consultation updateConsultation(Long consultationId, Consultation consultation);
    void deleteConsultation(Long consultationId);
    Consultation getConsultationById(Long consultationId);
    List<Consultation> getAllConsultations();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<Consultation> findByDossierMedicalId(Long dossierId);
    List<Consultation> findByStatut(StatutConsultation statut);
    List<Consultation> findByDate(LocalDate date);
    List<Consultation> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Consultation> findByDossierAndDate(Long dossierId, LocalDate date);

    // ========== GESTION STATUT ==========
    void changeStatut(Long consultationId, StatutConsultation nouveauStatut);

    // ========== VALIDATION ==========
    boolean existsById(Long consultationId);

    // ========== STATISTIQUES ==========
    long countAllConsultations();
    long countByStatut(StatutConsultation statut);
    long countByDossierMedicalId(Long dossierId);
}

