package ma.whitecare.service.modules.dossierMedical.api;

import ma.whitecare.entities.medical.DossierMedicale;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalService {

    // ========== CRUD DOSSIER MÉDICAL ==========
    DossierMedicale createDossierMedical(DossierMedicale dossierMedical);
    DossierMedicale updateDossierMedical(Long dossierId, DossierMedicale dossierMedical);
    void deleteDossierMedical(Long dossierId);
    DossierMedicale getDossierMedicalById(Long dossierId);
    List<DossierMedicale> getAllDossiersMedicaux();

    // ========== RECHERCHES SPÉCIFIQUES ==========
    List<DossierMedicale> findByPatientId(Long patientId);
    List<DossierMedicale> findByMedecinId(Long medecinId);
    List<DossierMedicale> findByDateCreation(LocalDate date);
    List<DossierMedicale> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    // ========== VALIDATION ==========
    boolean existsByPatientId(Long patientId);
    boolean existsById(Long dossierId);

    // ========== STATISTIQUES ==========
    long countAllDossiers();
    long countByPatientId(Long patientId);
    long countByMedecinId(Long medecinId);
}

